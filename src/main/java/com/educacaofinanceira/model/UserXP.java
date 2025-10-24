package com.educacaofinanceira.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_xp")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserXP {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // User CHILD

    @Column(nullable = false)
    private Integer currentLevel = 1; // Nível atual (1-10)

    @Column(nullable = false)
    private Integer currentXp = 0; // XP no nível atual

    @Column(nullable = false)
    private Integer totalXp = 0; // XP total acumulado

    @Column
    private LocalDateTime lastLevelUpAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
        if (currentLevel == null) currentLevel = 1;
        if (currentXp == null) currentXp = 0;
        if (totalXp == null) totalXp = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
