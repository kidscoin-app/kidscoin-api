package com.educacaofinanceira.service;

import com.educacaofinanceira.model.Task;
import com.educacaofinanceira.model.TaskAssignment;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.RecurrenceType;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.model.enums.TaskStatus;
import com.educacaofinanceira.repository.TaskAssignmentRepository;
import com.educacaofinanceira.repository.TaskRepository;
import com.educacaofinanceira.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável por criar automaticamente TaskAssignments
 * para tarefas recorrentes baseado na configuração de cada tarefa.
 *
 * Executa diariamente à meia-noite (00:00).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecurringTaskScheduler {

    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Job agendado que roda diariamente à meia-noite (00:00)
     * Cria automaticamente assignments para tarefas recorrentes
     */
    @Scheduled(cron = "0 0 0 * * *") // Meia-noite todos os dias
    @Transactional
    public void createRecurringTaskAssignments() {
        log.info("Iniciando criação de tarefas recorrentes para hoje: {}", LocalDate.now());

        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();

        // Buscar todas as tarefas recorrentes ativas
        List<Task> recurringTasks = taskRepository.findActiveRecurringTasks(TaskStatus.ACTIVE, today);

        log.info("Encontradas {} tarefas recorrentes ativas", recurringTasks.size());

        int assignmentsCreated = 0;

        for (Task task : recurringTasks) {
            // Verificar se a tarefa deve ser criada hoje baseado na configuração
            if (!shouldCreateAssignmentToday(task, currentDayOfWeek)) {
                log.debug("Tarefa '{}' não deve ser criada hoje ({})", task.getTitle(), currentDayOfWeek);
                continue;
            }

            // Buscar todas as crianças que deveriam receber essa tarefa
            // (assumindo que queremos buscar os assignments existentes para saber quais crianças)
            List<TaskAssignment> existingAssignments = taskAssignmentRepository.findByTaskId(task.getId());

            // Obter IDs únicos das crianças que já receberam essa tarefa
            List<UUID> childrenIds = existingAssignments.stream()
                    .map(assignment -> assignment.getAssignedToChild().getId())
                    .distinct()
                    .toList();

            // Para cada criança, verificar se já tem assignment de hoje e criar se necessário
            for (UUID childId : childrenIds) {
                if (shouldCreateAssignmentForChild(task.getId(), childId)) {
                    createAssignment(task, childId);
                    assignmentsCreated++;
                }
            }
        }

        log.info("Criadas {} novas atribuições de tarefas recorrentes", assignmentsCreated);
    }

    /**
     * Verifica se a tarefa deve ser criada hoje baseado no tipo de recorrência
     */
    private boolean shouldCreateAssignmentToday(Task task, DayOfWeek currentDayOfWeek) {
        if (task.getRecurrenceType() == RecurrenceType.DAILY) {
            // Tarefa diária sempre cria
            return true;
        }

        if (task.getRecurrenceType() == RecurrenceType.WEEKLY) {
            // Verificar se hoje é um dos dias configurados
            if (task.getRecurrenceDays() == null || task.getRecurrenceDays().isEmpty()) {
                return false;
            }

            // Converter o dia atual para o formato esperado (MON, TUE, etc)
            String todayCode = convertDayOfWeekToCode(currentDayOfWeek);

            // Verificar se o dia atual está na lista de dias configurados
            List<String> configuredDays = Arrays.asList(task.getRecurrenceDays().split(","));
            return configuredDays.contains(todayCode);
        }

        return false;
    }

    /**
     * Verifica se já existe um assignment PENDING ou COMPLETED criado hoje
     * para evitar duplicatas
     */
    private boolean shouldCreateAssignmentForChild(UUID taskId, UUID childId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        boolean alreadyExists = taskAssignmentRepository.existsActiveAssignmentForTaskAndChildToday(
                taskId, childId, startOfDay, endOfDay);

        return !alreadyExists; // Cria apenas se NÃO existir
    }

    /**
     * Cria um novo TaskAssignment para a criança
     */
    private void createAssignment(Task task, UUID childId) {
        User child = userRepository.findById(childId).orElse(null);

        if (child == null) {
            log.warn("Criança com ID {} não encontrada ao criar tarefa recorrente", childId);
            return;
        }

        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setAssignedToChild(child);
        taskAssignmentRepository.save(assignment);

        log.info("Criado assignment de tarefa recorrente '{}' para criança '{}'",
                task.getTitle(), child.getFullName());

        // Notificar criança
        notificationService.create(childId, NotificationType.TASK_ASSIGNED,
                "Nova tarefa disponível",
                "Você recebeu uma tarefa: " + task.getTitle(),
                ReferenceType.TASK, assignment.getId());
    }

    /**
     * Converte DayOfWeek para código de 3 letras (MON, TUE, etc)
     */
    private String convertDayOfWeekToCode(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> "MON";
            case TUESDAY -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY -> "THU";
            case FRIDAY -> "FRI";
            case SATURDAY -> "SAT";
            case SUNDAY -> "SUN";
        };
    }
}
