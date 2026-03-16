package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.response.BadgeResponse;
import com.educacaofinanceira.dto.response.GamificationResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.model.Badge;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.UserBadge;
import com.educacaofinanceira.model.UserXP;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.repository.BadgeRepository;
import com.educacaofinanceira.repository.UserBadgeRepository;
import com.educacaofinanceira.repository.UserRepository;
import com.educacaofinanceira.repository.UserXPRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Classe auxiliar para retornar resultado da gamificação
 */
class GamificationResult {
    boolean leveledUp;
    int newLevel;
    List<Badge> unlockedBadges;

    GamificationResult(boolean leveledUp, int newLevel, List<Badge> unlockedBadges) {
        this.leveledUp = leveledUp;
        this.newLevel = newLevel;
        this.unlockedBadges = unlockedBadges;
    }
}

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final UserXPRepository userXPRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;
    private final NotificationService notificationService;

    /**
     * Adiciona XP para uma criança e verifica level up e badges
     */
    @Transactional
    public GamificationResult addXP(UUID childId, Integer xpAmount, String reason) {
        // Buscar UserXP
        UserXP userXP = userXPRepository.findByUserId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("UserXP não encontrado"));

        // Adicionar XP
        userXP.setTotalXp(userXP.getTotalXp() + xpAmount);

        // Verificar subida de nível
        boolean leveledUp = false;
        int newLevel = userXP.getCurrentLevel();

        while (newLevel < 10) {
            int xpNeededForNextLevel = calculateXPForLevel(newLevel + 1);
            if (userXP.getTotalXp() >= xpNeededForNextLevel) {
                newLevel++;
                userXP.setCurrentLevel(newLevel);
                userXP.setLastLevelUpAt(LocalDateTime.now());
                leveledUp = true;
            } else {
                break;
            }
        }

        // Calcular currentXp (XP no nível atual)
        int xpForCurrentLevel = calculateXPForLevel(newLevel);
        userXP.setCurrentXp(userXP.getTotalXp() - xpForCurrentLevel);

        userXPRepository.save(userXP);

        // Verificar badges
        List<Badge> unlockedBadges = badgeService.checkAndUnlock(childId);

        // Se desbloqueou badges, adicionar XP bônus (recursivo)
        if (!unlockedBadges.isEmpty()) {
            int bonusXP = unlockedBadges.stream()
                    .mapToInt(Badge::getXpBonus)
                    .sum();

            if (bonusXP > 0) {
                // Chamada recursiva com XP bônus
                return addXP(childId, bonusXP, "Bônus de badges");
            }
        }

        // Criar notificações
        if (leveledUp) {
            notificationService.create(childId, NotificationType.LEVEL_UP,
                    "Subiu de nível!",
                    "Parabéns! Você chegou ao nível " + newLevel,
                    null, null);
        }

        for (Badge badge : unlockedBadges) {
            notificationService.create(childId, NotificationType.BADGE_UNLOCKED,
                    "Nova conquista!",
                    "Você desbloqueou: " + badge.getName(),
                    ReferenceType.TASK, badge.getId());
        }

        return new GamificationResult(leveledUp, newLevel, unlockedBadges);
    }

    /**
     * Atualiza o streak de tarefas diárias de uma criança.
     * - Se a última tarefa foi ontem: incrementa streak
     * - Se a última tarefa foi hoje: mantém streak (já contabilizado)
     * - Se passou mais de 1 dia: reseta streak para 1
     * - Atualiza longestStreak se o atual ultrapassar o recorde
     */
    @Transactional
    public void updateStreak(UUID childId) {
        UserXP userXP = userXPRepository.findByUserId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("UserXP não encontrado"));

        LocalDate today = LocalDate.now();
        LocalDate lastTaskDate = userXP.getLastTaskCompletedDate();

        if (lastTaskDate == null) {
            // Primeira tarefa aprovada
            userXP.setCurrentStreak(1);
        } else if (lastTaskDate.equals(today)) {
            // Já fez tarefa hoje, não incrementa (mantém streak)
            return;
        } else if (lastTaskDate.equals(today.minusDays(1))) {
            // Fez tarefa ontem, incrementa streak
            userXP.setCurrentStreak(userXP.getCurrentStreak() + 1);
        } else {
            // Passou mais de 1 dia sem fazer tarefa, reseta streak
            userXP.setCurrentStreak(1);
        }

        // Atualiza recorde se necessário
        if (userXP.getCurrentStreak() > userXP.getLongestStreak()) {
            userXP.setLongestStreak(userXP.getCurrentStreak());
        }

        // Atualiza data da última tarefa
        userXP.setLastTaskCompletedDate(today);

        userXPRepository.save(userXP);
    }

    /**
     * Calcula XP total necessário para atingir um nível
     * Fórmula: Para cada nível i (de 1 até level): i * 100 + (i-1) * 50
     */
    public int calculateXPForLevel(int level) {
        if (level <= 1) return 0;

        int totalXP = 0;
        for (int i = 1; i < level; i++) {
            totalXP += i * 100 + (i - 1) * 50;
        }
        return totalXP;
    }

    /**
     * 🔧 DEBUG: Corrige badge Milionário com problema de encoding
     * ⚠️ REMOVER ANTES DE PRODUÇÃO FINAL!
     */
    @Transactional
    public String fixMilionarioBadge() {
        List<Badge> allBadges = badgeRepository.findAll();

        // Buscar badge com nome contendo "Milion" (pega o corrompido)
        Badge milionarioBadge = allBadges.stream()
                .filter(b -> b.getName().contains("Milion"))
                .findFirst()
                .orElse(null);

        if (milionarioBadge == null) {
            return "❌ Badge Milionário não encontrada no banco";
        }

        String oldName = milionarioBadge.getName();
        milionarioBadge.setName("Milionário");
        badgeRepository.save(milionarioBadge);

        return "✅ Badge corrigida! Nome antigo: '" + oldName + "' → Novo: 'Milionário'";
    }

    /**
     * 🔧 DEBUG: Desbloqueia uma badge manualmente para testes
     * ⚠️ REMOVER ANTES DE PRODUÇÃO FINAL!
     */
    @Transactional
    public String unlockBadgeForTest(String username, String badgeName) {
        // Buscar criança pelo username
        User child = userRepository.findByUsername(username)
                .orElse(null);

        if (child == null) {
            return "❌ Criança não encontrada com username: " + username;
        }

        // Buscar badge pelo nome
        List<Badge> allBadges = badgeRepository.findAll();
        Badge targetBadge = allBadges.stream()
                .filter(b -> b.getName().equalsIgnoreCase(badgeName))
                .findFirst()
                .orElse(null);

        if (targetBadge == null) {
            return "❌ Badge não encontrada: " + badgeName + ". Badges disponíveis: " +
                   allBadges.stream().map(Badge::getName).collect(Collectors.joining(", "));
        }

        // Verificar se já possui a badge
        boolean alreadyHas = userBadgeRepository.existsByUserIdAndBadgeId(child.getId(), targetBadge.getId());
        if (alreadyHas) {
            return "⚠️ Criança já possui a badge: " + badgeName;
        }

        // Desbloquear badge
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(child);
        userBadge.setBadge(targetBadge);
        userBadge.setUnlockedAt(LocalDateTime.now());
        userBadgeRepository.save(userBadge);

        // Adicionar XP bônus da badge
        if (targetBadge.getXpBonus() != null && targetBadge.getXpBonus() > 0) {
            addXP(child.getId(), targetBadge.getXpBonus(), "Bônus da badge: " + badgeName);
        }

        // Criar notificação
        notificationService.create(child.getId(), NotificationType.BADGE_UNLOCKED,
                "Nova conquista desbloqueada!",
                "Você desbloqueou: " + targetBadge.getName(),
                ReferenceType.TASK, targetBadge.getId());

        return "✅ Badge '" + badgeName + "' desbloqueada com sucesso para " + child.getFullName() +
               " (+" + targetBadge.getXpBonus() + " XP)!";
    }

    /**
     * Busca dados de gamificação de uma criança
     */
    public GamificationResponse getGamification(UUID childId, User requestingUser) {
        // Buscar UserXP
        UserXP userXP = userXPRepository.findByUserId(childId)
                .orElseThrow(() -> new ResourceNotFoundException("UserXP não encontrado"));

        // Calcular XP necessário para próximo nível
        int xpForNextLevel;
        if (userXP.getCurrentLevel() >= 10) {
            xpForNextLevel = userXP.getTotalXp(); // Nível máximo
        } else {
            xpForNextLevel = calculateXPForLevel(userXP.getCurrentLevel() + 1);
        }

        // Calcular streak atual (verificar se precisa resetar)
        int currentStreak = calculateCurrentStreak(userXP);

        // Buscar badges desbloqueadas
        List<UserBadge> userBadges = userBadgeRepository.findByUserId(childId);
        Map<UUID, LocalDateTime> unlockedBadgesMap = userBadges.stream()
                .collect(Collectors.toMap(
                        ub -> ub.getBadge().getId(),
                        UserBadge::getUnlockedAt
                ));

        // Buscar todas as badges e marcar quais estão desbloqueadas
        List<Badge> allBadges = badgeRepository.findAll();
        List<BadgeResponse> badgeResponses = allBadges.stream()
                .map(badge -> {
                    boolean unlocked = unlockedBadgesMap.containsKey(badge.getId());
                    LocalDateTime unlockedAt = unlocked ? unlockedBadgesMap.get(badge.getId()) : null;
                    return BadgeResponse.fromBadge(badge, unlocked, unlockedAt);
                })
                .collect(Collectors.toList());

        return new GamificationResponse(
                userXP.getCurrentLevel(),
                userXP.getCurrentXp(),
                userXP.getTotalXp(),
                xpForNextLevel,
                currentStreak,
                userXP.getLongestStreak(),
                badgeResponses
        );
    }

    /**
     * Calcula o streak atual considerando se já passou mais de 1 dia desde a última tarefa.
     * Retorna 0 se o streak foi quebrado (mais de 1 dia sem tarefa).
     */
    private int calculateCurrentStreak(UserXP userXP) {
        LocalDate lastTaskDate = userXP.getLastTaskCompletedDate();
        if (lastTaskDate == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        // Se a última tarefa foi hoje ou ontem, o streak ainda está ativo
        if (lastTaskDate.equals(today) || lastTaskDate.equals(today.minusDays(1))) {
            return userXP.getCurrentStreak();
        }

        // Passou mais de 1 dia, streak foi quebrado
        return 0;
    }
}
