package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Redemption;
import com.educacaofinanceira.model.enums.RedemptionStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RedemptionResponse {

    private UUID id;
    private RewardResponse reward;
    private UUID childId;
    private String childName;
    private RedemptionStatus status;
    private Integer coinAmount; // Quantidade de moedas debitadas no pedido
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private String reviewedByName;
    private String rejectionReason;

    public static RedemptionResponse fromRedemption(Redemption redemption) {
        RedemptionResponse response = new RedemptionResponse();
        response.setId(redemption.getId());
        response.setReward(RewardResponse.fromReward(redemption.getReward()));
        response.setChildId(redemption.getChild().getId());
        response.setChildName(redemption.getChild().getFullName());
        response.setStatus(redemption.getStatus());
        response.setCoinAmount(redemption.getCoinAmount());
        response.setRequestedAt(redemption.getRequestedAt());
        response.setReviewedAt(redemption.getReviewedAt());
        if (redemption.getReviewedBy() != null) {
            response.setReviewedByName(redemption.getReviewedBy().getFullName());
        }
        response.setRejectionReason(redemption.getRejectionReason());
        return response;
    }
}
