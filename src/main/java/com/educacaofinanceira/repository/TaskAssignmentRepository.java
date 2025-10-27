package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.TaskAssignment;
import com.educacaofinanceira.model.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {

    List<TaskAssignment> findByAssignedToChildId(UUID childId);

    List<TaskAssignment> findByTaskId(UUID taskId);

    List<TaskAssignment> findByStatus(AssignmentStatus status);

    long countByAssignedToChildIdAndStatus(UUID childId, AssignmentStatus status);

    // Contar tarefas aprovadas em um dia específico
    long countByAssignedToChildIdAndStatusAndApprovedAtBetween(
        UUID childId,
        AssignmentStatus status,
        LocalDateTime start,
        LocalDateTime end
    );

    // Buscar tarefas ordenadas por data de aprovação
    List<TaskAssignment> findByAssignedToChildIdAndStatusOrderByApprovedAtDesc(
        UUID childId,
        AssignmentStatus status
    );

    // Buscar tarefas aprovadas em um período (para streak)
    List<TaskAssignment> findByAssignedToChildIdAndStatusAndApprovedAtBetween(
        UUID childId,
        AssignmentStatus status,
        LocalDateTime start,
        LocalDateTime end
    );

    /**
     * Verifica se já existe um assignment PENDING ou COMPLETED para uma tarefa e criança
     * criado no dia atual (usado para tarefas recorrentes)
     */
    @Query("SELECT CASE WHEN COUNT(ta) > 0 THEN true ELSE false END FROM TaskAssignment ta " +
           "WHERE ta.task.id = :taskId " +
           "AND ta.assignedToChild.id = :childId " +
           "AND ta.status IN ('PENDING', 'COMPLETED') " +
           "AND ta.createdAt >= :startOfDay AND ta.createdAt < :endOfDay")
    boolean existsActiveAssignmentForTaskAndChildToday(
        @Param("taskId") UUID taskId,
        @Param("childId") UUID childId,
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );
}
