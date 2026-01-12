package com.educacaofinanceira.model;

import com.educacaofinanceira.model.enums.SavingsDepositStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "savings_deposits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "savings_id", nullable = false)
    private Savings savings;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SavingsDepositStatus status = SavingsDepositStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime depositedAt;

    @Column
    private LocalDateTime withdrawnAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        // Só define depositedAt se não foi definido manualmente
        if (depositedAt == null) {
            depositedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = SavingsDepositStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
