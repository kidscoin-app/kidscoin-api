package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.request.CreateRedemptionRequest;
import com.educacaofinanceira.dto.response.RedemptionResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.Redemption;
import com.educacaofinanceira.model.Reward;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.Wallet;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.RedemptionStatus;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.RedemptionRepository;
import com.educacaofinanceira.repository.RewardRepository;
import com.educacaofinanceira.repository.UserRepository;
import com.educacaofinanceira.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedemptionService {

    private final RedemptionRepository redemptionRepository;
    private final RewardRepository rewardRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final NotificationService notificationService;

    /**
     * Criança solicita resgate de recompensa
     * Moedas NÃO são debitadas neste momento (apenas na aprovação)
     */
    @Transactional
    public RedemptionResponse requestRedemption(CreateRedemptionRequest request, User child) {
        // Buscar recompensa
        Reward reward = rewardRepository.findById(request.getRewardId())
                .orElseThrow(() -> new ResourceNotFoundException("Recompensa não encontrada"));

        // Validar que recompensa é da família da criança
        if (!reward.getFamily().getId().equals(child.getFamily().getId())) {
            throw new UnauthorizedException("Esta recompensa não está disponível para você");
        }

        // Validar que recompensa está ativa
        if (!reward.getIsActive()) {
            throw new IllegalStateException("Esta recompensa não está mais disponível");
        }

        // Validar saldo (mesmo sem debitar, avisar se não tem)
        Wallet wallet = walletRepository.findByChildId(child.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        if (wallet.getBalance() < reward.getCoinCost()) {
            throw new IllegalArgumentException("Saldo insuficiente. Você precisa de " +
                    reward.getCoinCost() + " moedas, mas tem apenas " + wallet.getBalance());
        }

        // Criar Redemption PENDING
        Redemption redemption = new Redemption();
        redemption.setReward(reward);
        redemption.setChild(child);
        redemption.setStatus(RedemptionStatus.PENDING);
        redemption = redemptionRepository.save(redemption);

        // Notificar pais da família
        List<User> parents = userRepository.findByFamilyIdAndRole(
                child.getFamily().getId(), UserRole.PARENT);

        for (User parent : parents) {
            notificationService.create(parent.getId(),
                    NotificationType.REDEMPTION_REQUESTED,
                    "Resgate solicitado",
                    child.getFullName() + " solicitou: " + reward.getName() +
                            " (" + reward.getCoinCost() + " moedas)",
                    ReferenceType.REWARD, redemption.getId());
        }

        // Forçar carregamento de relacionamentos lazy dentro da transação
        redemption.getReward().getCreatedBy().getFullName();
        redemption.getReward().getFamily().getId();
        redemption.getChild().getFullName();

        return RedemptionResponse.fromRedemption(redemption);
    }

    /**
     * Lista resgates
     * - PARENT: todos os resgates da família
     * - CHILD: apenas seus próprios resgates
     */
    @Transactional(readOnly = true)
    public List<RedemptionResponse> getRedemptions(User user, RedemptionStatus status) {
        List<Redemption> redemptions;

        if (user.getRole() == UserRole.PARENT) {
            // Pai vê todos da família
            if (status != null) {
                redemptions = redemptionRepository.findByStatus(status).stream()
                        .filter(r -> r.getReward().getFamily().getId().equals(user.getFamily().getId()))
                        .collect(Collectors.toList());
            } else {
                // Buscar todos e filtrar por família
                redemptions = redemptionRepository.findAll().stream()
                        .filter(r -> r.getReward().getFamily().getId().equals(user.getFamily().getId()))
                        .collect(Collectors.toList());
            }
        } else {
            // Criança vê apenas seus
            redemptions = redemptionRepository.findByChildId(user.getId());
            if (status != null) {
                redemptions = redemptions.stream()
                        .filter(r -> r.getStatus() == status)
                        .collect(Collectors.toList());
            }
        }

        // Forçar carregamento de relacionamentos lazy dentro da transação
        redemptions.forEach(redemption -> {
            redemption.getReward().getCreatedBy().getFullName();
            redemption.getReward().getFamily().getId();
            redemption.getChild().getFullName();
            if (redemption.getReviewedBy() != null) {
                redemption.getReviewedBy().getFullName();
            }
        });

        return redemptions.stream()
                .map(RedemptionResponse::fromRedemption)
                .collect(Collectors.toList());
    }

    /**
     * Pai aprova resgate
     * AGORA debita as moedas
     */
    @Transactional
    public RedemptionResponse approveRedemption(UUID redemptionId, User parent) {
        Redemption redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resgate não encontrado"));

        // Validar acesso
        if (parent.getRole() != UserRole.PARENT ||
            !redemption.getReward().getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não tem permissão para aprovar este resgate");
        }

        // Validar status
        if (redemption.getStatus() != RedemptionStatus.PENDING) {
            throw new IllegalStateException("Este resgate não está pendente");
        }

        // Debitar moedas
        UUID childId = redemption.getChild().getId();
        Integer coinCost = redemption.getReward().getCoinCost();

        try {
            walletService.debit(childId, coinCost,
                    "Resgate aprovado: " + redemption.getReward().getName(),
                    ReferenceType.REWARD, redemptionId);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Não foi possível aprovar: " + e.getMessage());
        }

        // Atualizar redemption
        redemption.setStatus(RedemptionStatus.APPROVED);
        redemption.setReviewedAt(LocalDateTime.now());
        redemption.setReviewedBy(parent);
        redemption = redemptionRepository.save(redemption);

        // Notificar criança
        notificationService.create(childId, NotificationType.REDEMPTION_APPROVED,
                "Resgate aprovado!",
                "Seu resgate foi aprovado: " + redemption.getReward().getName(),
                ReferenceType.REWARD, redemptionId);

        // Forçar carregamento de relacionamentos lazy dentro da transação
        redemption.getReward().getCreatedBy().getFullName();
        redemption.getReward().getFamily().getId();
        redemption.getChild().getFullName();
        redemption.getReviewedBy().getFullName();

        return RedemptionResponse.fromRedemption(redemption);
    }

    /**
     * Pai rejeita resgate
     * Moedas NÃO são debitadas
     */
    @Transactional
    public RedemptionResponse rejectRedemption(UUID redemptionId, String rejectionReason, User parent) {
        Redemption redemption = redemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resgate não encontrado"));

        // Validar acesso
        if (parent.getRole() != UserRole.PARENT ||
            !redemption.getReward().getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não tem permissão para rejeitar este resgate");
        }

        // Validar status
        if (redemption.getStatus() != RedemptionStatus.PENDING) {
            throw new IllegalStateException("Este resgate não está pendente");
        }

        // Atualizar redemption
        redemption.setStatus(RedemptionStatus.REJECTED);
        redemption.setReviewedAt(LocalDateTime.now());
        redemption.setReviewedBy(parent);
        redemption.setRejectionReason(rejectionReason);
        redemption = redemptionRepository.save(redemption);

        // Notificar criança
        UUID childId = redemption.getChild().getId();
        notificationService.create(childId, NotificationType.REDEMPTION_REJECTED,
                "Resgate rejeitado",
                "Seu resgate foi rejeitado: " + redemption.getReward().getName() +
                        ". Motivo: " + rejectionReason,
                ReferenceType.REWARD, redemptionId);

        // Forçar carregamento de relacionamentos lazy dentro da transação
        redemption.getReward().getCreatedBy().getFullName();
        redemption.getReward().getFamily().getId();
        redemption.getChild().getFullName();
        redemption.getReviewedBy().getFullName();

        return RedemptionResponse.fromRedemption(redemption);
    }
}
