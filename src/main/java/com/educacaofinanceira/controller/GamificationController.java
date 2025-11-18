package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.request.UnlockBadgeDebugRequest;
import com.educacaofinanceira.dto.response.GamificationResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.service.GamificationService;
import com.educacaofinanceira.util.SecurityHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/gamification")
@RequiredArgsConstructor
public class GamificationController {

    private final GamificationService gamificationService;
    private final SecurityHelper securityHelper;

    /**
     * Busca dados de gamificação (nível, XP, badges)
     * Query param: childId (opcional - se não fornecido, usa o próprio usuário)
     */
    @GetMapping
    public ResponseEntity<GamificationResponse> getGamification(
            @RequestParam(required = false) UUID childId) {
        User user = securityHelper.getAuthenticatedUser();

        // Se childId não for fornecido, assume que é o próprio usuário
        UUID targetChildId = (childId != null) ? childId : user.getId();

        GamificationResponse gamification = gamificationService.getGamification(targetChildId, user);
        return ResponseEntity.ok(gamification);
    }

    /**
     * 🔧 DEBUG: Desbloqueia uma badge manualmente para testes
     * ⚠️ REMOVER ANTES DE PRODUÇÃO FINAL!
     *
     * POST /api/gamification/debug/unlock
     * Body: { "username": "joaozinho", "badgeName": "Primeira Tarefa" }
     */
    @PostMapping("/debug/unlock")
    public ResponseEntity<String> unlockBadgeDebug(@Valid @RequestBody UnlockBadgeDebugRequest request) {
        String result = gamificationService.unlockBadgeForTest(request.getUsername(), request.getBadgeName());
        return ResponseEntity.ok(result);
    }

    /**
     * 🔧 DEBUG: Corrige badge Milionário com problema de encoding
     * ⚠️ REMOVER ANTES DE PRODUÇÃO FINAL!
     *
     * GET /api/gamification/debug/fix-milionario
     */
    @GetMapping("/debug/fix-milionario")
    public ResponseEntity<String> fixMilionarioBadge() {
        String result = gamificationService.fixMilionarioBadge();
        return ResponseEntity.ok(result);
    }
}
