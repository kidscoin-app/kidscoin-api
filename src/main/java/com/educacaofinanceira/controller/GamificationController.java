package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.response.GamificationResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.service.GamificationService;
import com.educacaofinanceira.util.SecurityHelper;
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
}
