package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.SavingsDeposit;
import com.educacaofinanceira.model.enums.SavingsDepositStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
public class SavingsDepositResponse {

    private UUID id;
    private Integer amount;
    private SavingsDepositStatus status;
    private LocalDateTime depositedAt;
    private LocalDateTime withdrawnAt;
    private Long daysHeld;
    private Integer currentInterest;
    private Integer currentTotal;
    private String bonusTier;

    public static SavingsDepositResponse fromSavingsDeposit(SavingsDeposit deposit, Integer calculatedInterest) {
        SavingsDepositResponse response = new SavingsDepositResponse();
        response.setId(deposit.getId());
        response.setAmount(deposit.getAmount());
        response.setStatus(deposit.getStatus());
        response.setDepositedAt(deposit.getDepositedAt());
        response.setWithdrawnAt(deposit.getWithdrawnAt());

        // Calcular dias guardado
        LocalDateTime endDate = deposit.getWithdrawnAt() != null
                ? deposit.getWithdrawnAt()
                : LocalDateTime.now();
        long daysHeld = ChronoUnit.DAYS.between(deposit.getDepositedAt(), endDate);
        response.setDaysHeld(daysHeld);

        // Informações de rendimento
        response.setCurrentInterest(calculatedInterest);
        response.setCurrentTotal(deposit.getAmount() + calculatedInterest);

        // Determinar tier de bônus
        response.setBonusTier(getBonusTierName(daysHeld));

        return response;
    }

    private static String getBonusTierName(long days) {
        if (days < 7) return "Base (100%)";
        if (days < 15) return "Bronze (110%)";
        if (days < 30) return "Prata (120%)";
        if (days < 60) return "Ouro (130%)";
        if (days < 90) return "Platina (140%)";
        return "Diamante (150%)";
    }
}
