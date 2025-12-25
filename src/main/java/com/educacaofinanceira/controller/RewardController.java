package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.request.CreateRewardRequest;
import com.educacaofinanceira.dto.response.RewardResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.service.RewardService;
import com.educacaofinanceira.util.SecurityHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;
    private final SecurityHelper securityHelper;

    /**
     * Cria uma nova recompensa (apenas PARENT)
     */
    @PostMapping
    public ResponseEntity<RewardResponse> createReward(@Valid @RequestBody CreateRewardRequest request) {
        User parent = securityHelper.getAuthenticatedUser();
        RewardResponse reward = rewardService.createReward(request, parent);
        return ResponseEntity.ok(reward);
    }

    /**
     * Lista recompensas da família
     * Query param: activeOnly (default: false para PARENT, true para CHILD)
     */
    @GetMapping
    public ResponseEntity<List<RewardResponse>> getRewards(
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        User user = securityHelper.getAuthenticatedUser();
        List<RewardResponse> rewards = rewardService.getRewards(user, activeOnly);
        return ResponseEntity.ok(rewards);
    }

    /**
     * Ativa/desativa uma recompensa (apenas PARENT)
     */
    @PatchMapping("/{rewardId}/toggle")
    public ResponseEntity<RewardResponse> toggleRewardStatus(@PathVariable UUID rewardId) {
        User parent = securityHelper.getAuthenticatedUser();
        RewardResponse reward = rewardService.toggleRewardStatus(rewardId, parent);
        return ResponseEntity.ok(reward);
    }

    /**
     * Deleta uma recompensa (apenas PARENT)
     */
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<Void> deleteReward(@PathVariable UUID rewardId) {
        User parent = securityHelper.getAuthenticatedUser();
        rewardService.deleteReward(rewardId, parent);
        return ResponseEntity.noContent().build();
    }
}
