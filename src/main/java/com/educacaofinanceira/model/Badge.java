package com.educacaofinanceira.model;

import com.educacaofinanceira.model.enums.BadgeCriteriaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 50)
    private String iconName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeCriteriaType criteriaType;

    @Column(nullable = false)
    private Integer criteriaValue; // Valor necessário para desbloquear

    @Column(nullable = false)
    private Integer xpBonus; // XP bônus ao desbloquear

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
