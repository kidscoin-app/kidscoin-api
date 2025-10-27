package com.educacaofinanceira.repository;

import com.educacaofinanceira.model.Task;
import com.educacaofinanceira.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByFamilyId(UUID familyId);

    /**
     * Busca todas as tarefas recorrentes ativas
     * (status ACTIVE, isRecurring = true, e sem data de término ou data de término futura)
     */
    @Query("SELECT t FROM Task t " +
           "WHERE t.status = :status " +
           "AND t.isRecurring = true " +
           "AND (t.recurrenceEndDate IS NULL OR t.recurrenceEndDate >= :today)")
    List<Task> findActiveRecurringTasks(
        @Param("status") TaskStatus status,
        @Param("today") LocalDate today
    );
}
