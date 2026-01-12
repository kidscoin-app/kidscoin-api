package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.response.SavingsResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.Savings;
import com.educacaofinanceira.model.SavingsDeposit;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.model.enums.SavingsDepositStatus;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.SavingsDepositRepository;
import com.educacaofinanceira.repository.SavingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsService {

    private final SavingsRepository savingsRepository;
    private final SavingsDepositRepository savingsDepositRepository;
    private final WalletService walletService;
    private final NotificationService notificationService;

    // Constante: taxa base diária
    private static final double DAILY_RATE_BASE = 0.004; // 0,4% ao dia

    /**
     * Deposita moedas da carteira para a poupança
     * Cria um registro individual de depósito para rastreamento
     */
    @Transactional
    public SavingsResponse deposit(UUID childId, Integer amount, User requestingUser) {
        // Validar acesso
        validateAccess(childId, requestingUser);

        // Debitar da carteira
        walletService.debit(childId, amount,
                "Depósito na poupança",
                ReferenceType.SAVINGS, null);

        // Buscar ou criar poupança
        Savings savings = savingsRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

        // Criar registro de depósito individual
        SavingsDeposit deposit = new SavingsDeposit();
        deposit.setSavings(savings);
        deposit.setAmount(amount);
        deposit.setStatus(SavingsDepositStatus.ACTIVE);
        savingsDepositRepository.save(deposit);

        // Atualizar totais da poupança
        savings.setBalance(savings.getBalance() + amount);
        savings.setTotalDeposited(savings.getTotalDeposited() + amount);
        savings.setLastDepositAt(LocalDateTime.now());
        savingsRepository.save(savings);

        // Notificar criança
        notificationService.create(childId, NotificationType.SAVINGS_DEPOSIT,
                "Depósito realizado",
                "Você depositou " + amount + " moedas na poupança!",
                ReferenceType.SAVINGS, savings.getId());

        return buildSavingsResponse(savings);
    }

    /**
     * Saca moedas da poupança para a carteira
     * Usa FIFO (First In, First Out) para determinar quais depósitos sacar primeiro
     * Calcula rendimento individual de cada depósito com bônus progressivo por faixas
     */
    @Transactional
    public SavingsResponse withdraw(UUID childId, Integer amount, User requestingUser) {
        // Validar acesso
        validateAccess(childId, requestingUser);

        // Buscar poupança
        Savings savings = savingsRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

        // Buscar depósitos ativos ordenados por data (FIFO)
        List<SavingsDeposit> activeDeposits = savingsDepositRepository
                .findBySavingsIdAndStatusOrderByDepositedAtAsc(savings.getId(), SavingsDepositStatus.ACTIVE);

        if (activeDeposits.isEmpty()) {
            throw new IllegalArgumentException("Não há depósitos ativos na poupança");
        }

        // Calcular saldo total disponível (principal + rendimentos)
        int totalAvailable = calculateTotalBalance(activeDeposits);

        // Validar saldo
        if (totalAvailable < amount) {
            throw new IllegalArgumentException("Saldo insuficiente na poupança. Disponível: " +
                    totalAvailable + " moedas, solicitado: " + amount + " moedas");
        }

        // Processar saque usando FIFO
        int remainingToWithdraw = amount;
        int totalInterestEarned = 0;
        int totalPrincipalWithdrawn = 0;
        int depositsProcessed = 0;

        for (SavingsDeposit deposit : activeDeposits) {
            if (remainingToWithdraw <= 0) {
                break;
            }

            // Calcular valor total deste depósito (principal + rendimentos)
            long daysHeld = ChronoUnit.DAYS.between(deposit.getDepositedAt(), LocalDateTime.now());
            int interest = calculateInterestByTiers(deposit.getAmount(), daysHeld);
            int depositTotal = deposit.getAmount() + interest;

            if (depositTotal <= remainingToWithdraw) {
                // Saca o depósito inteiro
                deposit.setStatus(SavingsDepositStatus.WITHDRAWN);
                deposit.setWithdrawnAt(LocalDateTime.now());
                savingsDepositRepository.save(deposit);

                totalPrincipalWithdrawn += deposit.getAmount();
                totalInterestEarned += interest;
                remainingToWithdraw -= depositTotal;
                depositsProcessed++;

                log.info("SAQUE COMPLETO: {} moedas principal + {} juros ({} dias)",
                        deposit.getAmount(), interest, daysHeld);
            } else {
                // Saque parcial do depósito
                // Calcula proporção: quanto do principal e quanto do rendimento sacar
                double proportion = (double) remainingToWithdraw / depositTotal;
                int principalToWithdraw = (int) Math.ceil(deposit.getAmount() * proportion);
                int interestToWithdraw = remainingToWithdraw - principalToWithdraw;

                // Cria novo depósito com o saldo restante
                int remainingPrincipal = deposit.getAmount() - principalToWithdraw;
                if (remainingPrincipal > 0) {
                    SavingsDeposit newDeposit = new SavingsDeposit();
                    newDeposit.setSavings(savings);
                    newDeposit.setAmount(remainingPrincipal);
                    newDeposit.setStatus(SavingsDepositStatus.ACTIVE);
                    // Mantém a data original do depósito para cálculo correto dos juros
                    newDeposit.setDepositedAt(deposit.getDepositedAt());
                    savingsDepositRepository.save(newDeposit);
                }

                // Marca depósito original como sacado
                deposit.setStatus(SavingsDepositStatus.WITHDRAWN);
                deposit.setWithdrawnAt(LocalDateTime.now());
                savingsDepositRepository.save(deposit);

                totalPrincipalWithdrawn += principalToWithdraw;
                totalInterestEarned += interestToWithdraw;
                remainingToWithdraw = 0;
                depositsProcessed++;

                log.info("SAQUE PARCIAL: {} principal + {} juros sacados | {} principal restante ({} dias)",
                        principalToWithdraw, interestToWithdraw, remainingPrincipal, daysHeld);
            }
        }

        // Atualizar poupança
        savings.setBalance(savings.getBalance() - totalPrincipalWithdrawn);
        savings.setTotalEarned(savings.getTotalEarned() + totalInterestEarned);
        savingsRepository.save(savings);

        // Creditar na carteira (principal + rendimentos)
        int totalAmount = totalPrincipalWithdrawn + totalInterestEarned;
        String description = totalInterestEarned > 0
                ? "Saque da poupança (+" + totalInterestEarned + " de juros)"
                : "Saque da poupança";

        walletService.credit(childId, totalAmount, description, ReferenceType.SAVINGS, savings.getId());

        // Notificar criança
        String message = "Você sacou " + totalPrincipalWithdrawn + " moedas da poupança";
        if (totalInterestEarned > 0) {
            message += " e ganhou " + totalInterestEarned + " moedas de juros!";
        }

        notificationService.create(childId, NotificationType.SAVINGS_WITHDRAWAL,
                "Saque realizado", message, ReferenceType.SAVINGS, savings.getId());

        log.info("═══ SAQUE FINALIZADO: {} moedas total (principal: {} + juros: {}) ═══",
                totalAmount, totalPrincipalWithdrawn, totalInterestEarned);

        return buildSavingsResponse(savings);
    }

    /**
     * Busca dados da poupança com cálculo de juros pendentes
     */
    @Transactional(readOnly = true)
    public SavingsResponse getSavings(UUID childId, User requestingUser) {
        validateAccess(childId, requestingUser);

        Savings savings = savingsRepository.findByChildId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

        return buildSavingsResponse(savings);
    }

    /**
     * Calcula o saldo total disponível somando todos os depósitos ativos + rendimentos
     */
    private int calculateTotalBalance(List<SavingsDeposit> activeDeposits) {
        int total = 0;
        for (SavingsDeposit deposit : activeDeposits) {
            long daysHeld = ChronoUnit.DAYS.between(deposit.getDepositedAt(), LocalDateTime.now());
            int interest = calculateInterestByTiers(deposit.getAmount(), daysHeld);
            total += deposit.getAmount() + interest;
        }
        return total;
    }

    /**
     * Calcula juros de um depósito usando sistema de faixas progressivas
     *
     * Taxa muda conforme o tempo:
     * - Dias 1-6:   0,4%/dia  (100% da taxa base)
     * - Dias 7-14:  0,44%/dia (110% da taxa base - bônus +10%)
     * - Dias 15-29: 0,48%/dia (120% da taxa base - bônus +20%)
     * - Dias 30-59: 0,52%/dia (130% da taxa base - bônus +30%)
     * - Dias 60-89: 0,56%/dia (140% da taxa base - bônus +40%)
     * - Dias 90+:   0,6%/dia  (150% da taxa base - bônus +50%)
     *
     * Exemplo: 1000 moedas por 90 dias
     * - Dias 1-6:   1000 × 0,004 × 6 = 24 moedas
     * - Dias 7-14:  1000 × 0,0044 × 8 = 35,2 moedas
     * - Dias 15-29: 1000 × 0,0048 × 15 = 72 moedas
     * - Dias 30-59: 1000 × 0,0052 × 30 = 156 moedas
     * - Dias 60-89: 1000 × 0,0056 × 30 = 168 moedas
     * - Dia 90:     1000 × 0,006 × 1 = 6 moedas
     * Total: 461,2 moedas
     */
    private int calculateInterestByTiers(int principal, long totalDays) {
        if (totalDays <= 0) {
            return 0;
        }

        double totalInterest = 0.0;
        long daysProcessed = 0;

        // Definir faixas [início, fim, multiplicador]
        int[][] tiers = {
                {1, 6, 100},      // Dias 1-6: 100% (taxa base)
                {7, 14, 110},     // Dias 7-14: 110% (+10%)
                {15, 29, 120},    // Dias 15-29: 120% (+20%)
                {30, 59, 130},    // Dias 30-59: 130% (+30%)
                {60, 89, 140},    // Dias 60-89: 140% (+40%)
                {90, Integer.MAX_VALUE, 150}  // Dias 90+: 150% (+50%)
        };

        for (int[] tier : tiers) {
            int tierStart = tier[0];
            int tierEnd = tier[1];
            int multiplierPercent = tier[2];

            // Determinar quantos dias caem nesta faixa
            long daysInTier = 0;
            if (totalDays > tierEnd) {
                // Passou completamente por esta faixa
                daysInTier = tierEnd - tierStart + 1;
            } else if (totalDays >= tierStart) {
                // Está nesta faixa (pode ser parcial)
                daysInTier = totalDays - tierStart + 1;
            } else {
                // Ainda não chegou nesta faixa
                break;
            }

            if (daysInTier > 0) {
                // Calcular juros desta faixa
                double tierRate = DAILY_RATE_BASE * (multiplierPercent / 100.0);
                double tierInterest = principal * tierRate * daysInTier;
                totalInterest += tierInterest;
                daysProcessed += daysInTier;
            }

            if (totalDays <= tierEnd) {
                break;
            }
        }

        // Arredondar para cima (beneficia a criança)
        return (int) Math.ceil(totalInterest);
    }

    /**
     * Valida acesso (PARENT da família ou própria CHILD)
     */
    private void validateAccess(UUID childId, User requestingUser) {
        if (requestingUser.getRole() == UserRole.PARENT) {
            // Pai deve ser da mesma família
            Savings savings = savingsRepository.findByChildId(childId)
                    .orElseThrow(() -> new ResourceNotFoundException("Poupança não encontrada"));

            if (!savings.getChild().getFamily().getId().equals(requestingUser.getFamily().getId())) {
                throw new UnauthorizedException("Você não tem acesso a esta poupança");
            }
        } else {
            // Criança só pode acessar própria poupança
            if (!childId.equals(requestingUser.getId())) {
                throw new UnauthorizedException("Você não tem acesso a esta poupança");
            }
        }
    }

    /**
     * Constrói SavingsResponse com cálculo de juros pendentes e saldo disponível
     */
    private SavingsResponse buildSavingsResponse(Savings savings) {
        // Buscar depósitos ativos para calcular juros pendentes
        List<SavingsDeposit> activeDeposits = savingsDepositRepository
                .findBySavingsIdAndStatusOrderByDepositedAtAsc(savings.getId(), SavingsDepositStatus.ACTIVE);

        // Calcular juros pendentes (não realizados)
        int pendingInterest = 0;
        for (SavingsDeposit deposit : activeDeposits) {
            long daysHeld = ChronoUnit.DAYS.between(deposit.getDepositedAt(), LocalDateTime.now());
            pendingInterest += calculateInterestByTiers(deposit.getAmount(), daysHeld);
        }

        // Calcular saldo disponível total (principal + juros pendentes)
        int availableBalance = savings.getBalance() + pendingInterest;

        SavingsResponse response = SavingsResponse.fromSavings(savings);
        response.setAvailableBalance(availableBalance);
        response.setPendingInterest(pendingInterest);

        return response;
    }
}
