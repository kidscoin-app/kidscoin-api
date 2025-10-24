package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.response.NotificationResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.service.NotificationService;
import com.educacaofinanceira.util.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SecurityHelper securityHelper;

    /**
     * Lista todas as notificações do usuário
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        User user = securityHelper.getAuthenticatedUser();
        List<NotificationResponse> notifications = notificationService.getUserNotifications(user.getId());
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marca uma notificação como lida
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Marca todas as notificações como lidas
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        User user = securityHelper.getAuthenticatedUser();
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Conta notificações não lidas
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> countUnread() {
        User user = securityHelper.getAuthenticatedUser();
        Long count = notificationService.countUnread(user.getId());
        return ResponseEntity.ok(count);
    }
}
