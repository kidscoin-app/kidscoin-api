package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.response.TransactionResponse;
import com.educacaofinanceira.dto.response.WalletResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.service.WalletService;
import com.educacaofinanceira.util.SecurityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final SecurityHelper securityHelper;

    /**
     * Busca carteira de uma criança
     * Query param: childId (opcional - se não fornecido, usa o próprio usuário)
     */
    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(@RequestParam(required = false) UUID childId) {
        User user = securityHelper.getAuthenticatedUser();

        // Se childId não for fornecido, assume que é o próprio usuário
        UUID targetChildId = (childId != null) ? childId : user.getId();

        WalletResponse wallet = walletService.getWallet(targetChildId, user);
        return ResponseEntity.ok(wallet);
    }

    /**
     * Busca histórico de transações
     * Query params:
     * - childId (opcional - se não fornecido, usa o próprio usuário)
     * - limit (padrão: 20)
     * - offset (padrão: 0)
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) UUID childId,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(defaultValue = "0") Integer offset) {
        User user = securityHelper.getAuthenticatedUser();

        // Se childId não for fornecido, assume que é o próprio usuário
        UUID targetChildId = (childId != null) ? childId : user.getId();

        List<TransactionResponse> transactions = walletService.getTransactions(
                targetChildId, user, limit, offset);
        return ResponseEntity.ok(transactions);
    }
}
