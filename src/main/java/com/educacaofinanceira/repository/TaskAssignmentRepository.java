package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.TaskAssignment;
import com.educacaofinanceira.model.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, UUID> {

    List<TaskAssignment> findByAssignedToChildId(UUID childId);

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
}
