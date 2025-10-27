package com.educacaofinanceira.model;

import com.educacaofinanceira.model.enums.RecurrenceType;
import com.educacaofinanceira.model.enums.TaskCategory;
import com.educacaofinanceira.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy; // User PARENT

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Integer coinValue;

    @Column(nullable = false)
    private Integer xpValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    // Campos de recorrência
    @Column(nullable = false)
    private Boolean isRecurring;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private RecurrenceType recurrenceType;

    /**
     * Dias da semana para recorrência semanal (separados por vírgula)
     * Exemplo: "MON,WED,FRI" ou "TUE,THU"
     * Valores possíveis: MON, TUE, WED, THU, FRI, SAT, SUN
     */
    @Column(length = 100, nullable = true)
    private String recurrenceDays;

    /**
     * Data de término da recorrência (opcional)
     * Se null, a tarefa é sempre ativa
     */
    @Column(nullable = true)
    private LocalDate recurrenceEndDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = TaskStatus.ACTIVE;
        }
        if (isRecurring == null) {
            isRecurring = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
