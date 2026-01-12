package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Savings;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SavingsResponse {

    private UUID id;
    private UUID childId;
    private String childName;
    private Integer balance; // Principal (sem juros acumulados)
    private Integer availableBalance; // Total disponível (principal + juros acumulados não realizados)
    private Integer pendingInterest; // Juros acumulados não realizados
    private Integer totalDeposited;
    private Integer totalEarned; // Juros já realizados (de saques anteriores)
    private LocalDateTime lastDepositAt;

    public static SavingsResponse fromSavings(Savings savings) {
        SavingsResponse response = new SavingsResponse();
        response.setId(savings.getId());
        response.setChildId(savings.getChild().getId());
        response.setChildName(savings.getChild().getFullName());
        response.setBalance(savings.getBalance());
        response.setTotalDeposited(savings.getTotalDeposited());
        response.setTotalEarned(savings.getTotalEarned());
        response.setLastDepositAt(savings.getLastDepositAt());
        // availableBalance e pendingInterest serão definidos pelo service
        return response;
    }
}
