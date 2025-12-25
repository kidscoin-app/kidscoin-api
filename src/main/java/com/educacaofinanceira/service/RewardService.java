package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.request.CreateRewardRequest;
import com.educacaofinanceira.dto.response.RewardResponse;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.Reward;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;

    /**
     * Cria uma nova recompensa (apenas PARENT)
     */
    @Transactional
    public RewardResponse createReward(CreateRewardRequest request, User parent) {
        // Validar que é um PARENT
        if (parent.getRole() != UserRole.PARENT) {
            throw new UnauthorizedException("Apenas pais podem criar recompensas");
        }

        // Criar Reward
        Reward reward = new Reward();
        reward.setFamily(parent.getFamily());
        reward.setCreatedBy(parent);
        reward.setName(request.getName());
        reward.setDescription(request.getDescription());
        reward.setCoinCost(request.getCoinCost());
        reward.setCategory(request.getCategory());
        reward.setImageUrl(request.getImageUrl());
        reward = rewardRepository.save(reward);

        // Forçar carregamento do relacionamento lazy dentro da transação
        reward.getCreatedBy().getFullName();
        reward.getFamily().getId();

        return RewardResponse.fromReward(reward);
    }

    /**
     * Lista recompensas da família
     * - PARENT: todas as recompensas
     * - CHILD: apenas recompensas ativas
     */
    @Transactional(readOnly = true)
    public List<RewardResponse> getRewards(User user, Boolean activeOnly) {
        List<Reward> rewards;

        if (user.getRole() == UserRole.PARENT && !activeOnly) {
            // Pai pode ver todas (usa JOIN FETCH para carregar relacionamentos)
            rewards = rewardRepository.findByFamilyIdWithRelations(user.getFamily().getId());
        } else {
            // Criança ou pai filtrando, vê apenas ativas (usa JOIN FETCH)
            rewards = rewardRepository.findByFamilyIdAndIsActiveWithRelations(
                    user.getFamily().getId(), true);
        }

        return rewards.stream()
                .map(RewardResponse::fromReward)
                .collect(Collectors.toList());
    }

    /**
     * Ativa/desativa uma recompensa (apenas PARENT)
     */
    @Transactional
    public RewardResponse toggleRewardStatus(UUID rewardId, User parent) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));

        // Validar acesso
        if (parent.getRole() != UserRole.PARENT ||
            !reward.getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não tem permissão para modificar esta recompensa");
        }

        // Toggle status
        reward.setIsActive(!reward.getIsActive());
        reward = rewardRepository.save(reward);

        // Forçar carregamento do relacionamento lazy dentro da transação
        reward.getCreatedBy().getFullName();
        reward.getFamily().getId();

        return RewardResponse.fromReward(reward);
    }

    /**
     * Deleta uma recompensa (apenas PARENT)
     */
    @Transactional
    public void deleteReward(UUID rewardId, User parent) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new IllegalArgumentException("Recompensa não encontrada"));

        // Validar acesso
        if (parent.getRole() != UserRole.PARENT ||
            !reward.getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não tem permissão para deletar esta recompensa");
        }

        // Deletar recompensa
        rewardRepository.delete(reward);
    }
}
