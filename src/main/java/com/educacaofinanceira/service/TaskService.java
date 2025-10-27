package com.educacaofinanceira.service;

import com.educacaofinanceira.dto.request.CreateTaskRequest;
import com.educacaofinanceira.dto.response.TaskAssignmentResponse;
import com.educacaofinanceira.dto.response.TaskResponse;
import com.educacaofinanceira.exception.ResourceNotFoundException;
import com.educacaofinanceira.exception.UnauthorizedException;
import com.educacaofinanceira.model.Task;
import com.educacaofinanceira.model.TaskAssignment;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.model.enums.AssignmentStatus;
import com.educacaofinanceira.model.enums.NotificationType;
import com.educacaofinanceira.model.enums.ReferenceType;
import com.educacaofinanceira.model.enums.UserRole;
import com.educacaofinanceira.repository.TaskAssignmentRepository;
import com.educacaofinanceira.repository.TaskRepository;
import com.educacaofinanceira.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;

    /**
     * Cria uma nova tarefa e atribui a crianças
     */
    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, User parent) {
        // Validar que é um PARENT
        if (parent.getRole() != UserRole.PARENT) {
            throw new UnauthorizedException("Apenas pais podem criar tarefas");
        }

        // Criar Task
        Task task = new Task();
        task.setFamily(parent.getFamily());
        task.setCreatedBy(parent);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCoinValue(request.getCoinValue());
        task.setXpValue(request.getXpValue());
        task.setCategory(request.getCategory());

        // Configurar recorrência
        task.setIsRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false);
        task.setRecurrenceType(request.getRecurrenceType());
        task.setRecurrenceDays(request.getRecurrenceDays());
        task.setRecurrenceEndDate(request.getRecurrenceEndDate());

        task = taskRepository.save(task);

        // Criar TaskAssignments para cada criança
        for (UUID childId : request.getChildrenIds()) {
            User child = userRepository.findById(childId)
                    .orElseThrow(() -> new ResourceNotFoundException("Criança não encontrada"));

            // Validar que a criança é da mesma família
            if (!child.getFamily().getId().equals(parent.getFamily().getId())) {
                throw new UnauthorizedException("Criança não pertence à sua família");
            }

            TaskAssignment assignment = new TaskAssignment();
            assignment.setTask(task);
            assignment.setAssignedToChild(child);
            taskAssignmentRepository.save(assignment);

            // Notificar criança
            notificationService.create(childId, NotificationType.TASK_ASSIGNED,
                    "Nova tarefa disponível",
                    "Você recebeu uma nova tarefa: " + task.getTitle(),
                    ReferenceType.TASK, assignment.getId());
        }

        return TaskResponse.fromTask(task);
    }

    /**
     * Lista tarefas
     * - PARENT: todas as assignments da família
     * - CHILD: apenas suas próprias assignments
     */
    @Transactional(readOnly = true)
    public List<TaskAssignmentResponse> getTasks(User user) {
        List<TaskAssignment> assignments;

        if (user.getRole() == UserRole.PARENT) {
            // Buscar todas as tasks da família
            List<Task> familyTasks = taskRepository.findByFamilyId(user.getFamily().getId());

            // Buscar todos os assignments dessas tasks
            assignments = new ArrayList<>();
            for (Task task : familyTasks) {
                List<TaskAssignment> taskAssignments = taskAssignmentRepository.findByTaskId(task.getId());
                assignments.addAll(taskAssignments);
            }
        } else {
            // Criança vê apenas suas próprias assignments
            assignments = taskAssignmentRepository.findByAssignedToChildId(user.getId());
        }

        return assignments.stream()
                .map(TaskAssignmentResponse::fromAssignment)
                .collect(Collectors.toList());
    }

    /**
     * Criança marca tarefa como concluída
     */
    @Transactional
    public TaskAssignmentResponse completeTask(UUID assignmentId, User child) {
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        // Validar que é a criança atribuída
        if (!assignment.getAssignedToChild().getId().equals(child.getId())) {
            throw new UnauthorizedException("Você não tem permissão para completar esta tarefa");
        }

        // Validar status (deve estar PENDING)
        if (assignment.getStatus() != AssignmentStatus.PENDING) {
            throw new IllegalStateException("Esta tarefa não pode ser marcada como concluída");
        }

        // Marcar como completada
        assignment.setStatus(AssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());
        assignment = taskAssignmentRepository.save(assignment);

        // Notificar pais da família
        List<User> parents = userRepository.findByFamilyIdAndRole(
                child.getFamily().getId(), UserRole.PARENT);

        for (User parent : parents) {
            notificationService.create(parent.getId(),
                    NotificationType.TASK_COMPLETED,
                    "Tarefa completada",
                    child.getFullName() + " completou: " + assignment.getTask().getTitle(),
                    ReferenceType.TASK, assignmentId);
        }

        return TaskAssignmentResponse.fromAssignment(assignment);
    }

    /**
     * Pai aprova tarefa
     * SEQUÊNCIA CRÍTICA: atualiza status → credita moedas → adiciona XP → verifica badges
     */
    @Transactional
    public TaskAssignmentResponse approveTask(UUID assignmentId, User parent) {
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        // Validar que é pai da família
        if (parent.getRole() != UserRole.PARENT ||
            !assignment.getTask().getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não tem permissão para aprovar esta tarefa");
        }

        // Validar status (deve estar COMPLETED)
        if (assignment.getStatus() != AssignmentStatus.COMPLETED) {
            throw new IllegalStateException("Esta tarefa não está aguardando aprovação");
        }

        // Marcar como aprovada
        assignment.setStatus(AssignmentStatus.APPROVED);
        assignment.setApprovedAt(LocalDateTime.now());
        assignment.setApprovedBy(parent);
        assignment = taskAssignmentRepository.save(assignment);

        UUID childId = assignment.getAssignedToChild().getId();
        Task task = assignment.getTask();

        // **SEQUÊNCIA CRÍTICA:**
        // 1. Creditar moedas
        walletService.credit(childId, task.getCoinValue(),
                "Tarefa aprovada: " + task.getTitle(),
                ReferenceType.TASK, assignmentId);

        // 2. Adicionar XP (que automaticamente verifica badges e níveis)
        gamificationService.addXP(childId, task.getXpValue(),
                "Tarefa aprovada: " + task.getTitle());

        // 3. Notificar criança
        notificationService.create(childId, NotificationType.TASK_APPROVED,
                "Tarefa aprovada!",
                "Você ganhou " + task.getCoinValue() + " moedas e " +
                        task.getXpValue() + " XP por completar: " + task.getTitle(),
                ReferenceType.TASK, assignmentId);

        return TaskAssignmentResponse.fromAssignment(assignment);
    }

    /**
     * Pai rejeita tarefa
     */
    @Transactional
    public TaskAssignmentResponse rejectTask(UUID assignmentId, String rejectionReason, User parent) {
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        // Validar que é pai da família
        if (parent.getRole() != UserRole.PARENT ||
            !assignment.getTask().getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não tem permissão para rejeitar esta tarefa");
        }

        // Validar status (deve estar COMPLETED)
        if (assignment.getStatus() != AssignmentStatus.COMPLETED) {
            throw new IllegalStateException("Esta tarefa não está aguardando aprovação");
        }

        // Marcar como rejeitada
        assignment.setStatus(AssignmentStatus.REJECTED);
        assignment.setRejectionReason(rejectionReason);
        assignment.setApprovedAt(LocalDateTime.now()); // Data de revisão
        assignment.setApprovedBy(parent);
        assignment = taskAssignmentRepository.save(assignment);

        UUID childId = assignment.getAssignedToChild().getId();

        // Notificar criança
        notificationService.create(childId, NotificationType.TASK_REJECTED,
                "Tarefa rejeitada",
                "Sua tarefa foi rejeitada: " + assignment.getTask().getTitle() +
                        ". Motivo: " + rejectionReason,
                ReferenceType.TASK, assignmentId);

        return TaskAssignmentResponse.fromAssignment(assignment);
    }

    /**
     * Criança tenta novamente uma tarefa rejeitada
     * REJECTED → PENDING + limpa campos de aprovação/rejeição
     */
    @Transactional
    public TaskAssignmentResponse retryTask(UUID assignmentId, User child) {
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        // Validar que é a criança atribuída
        if (!assignment.getAssignedToChild().getId().equals(child.getId())) {
            throw new UnauthorizedException("Você não tem permissão para tentar novamente esta tarefa");
        }

        // Validar status (deve estar REJECTED)
        if (assignment.getStatus() != AssignmentStatus.REJECTED) {
            throw new IllegalStateException("Apenas tarefas rejeitadas podem ser tentadas novamente");
        }

        // Resetar para PENDING e limpar campos
        assignment.setStatus(AssignmentStatus.PENDING);
        assignment.setCompletedAt(null);
        assignment.setApprovedAt(null);
        assignment.setApprovedBy(null);
        assignment.setRejectionReason(null);
        assignment = taskAssignmentRepository.save(assignment);

        return TaskAssignmentResponse.fromAssignment(assignment);
    }

    /**
     * Exclui uma tarefa atribuída (apenas PARENT)
     * Só pode excluir se status for PENDING ou REJECTED
     */
    @Transactional
    public void deleteTaskAssignment(UUID assignmentId, User parent) {
        TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        // Validar que é pai da família
        if (parent.getRole() != UserRole.PARENT ||
            !assignment.getTask().getFamily().getId().equals(parent.getFamily().getId())) {
            throw new UnauthorizedException("Você não tem permissão para excluir esta tarefa");
        }

        // Validar status - não pode excluir tarefas aprovadas ou completadas
        if (assignment.getStatus() == AssignmentStatus.APPROVED) {
            throw new IllegalStateException("Não é possível excluir uma tarefa já aprovada");
        }

        if (assignment.getStatus() == AssignmentStatus.COMPLETED) {
            throw new IllegalStateException("Não é possível excluir uma tarefa aguardando aprovação");
        }

        // Notificar criança se tarefa já estava atribuída
        UUID childId = assignment.getAssignedToChild().getId();
        String taskTitle = assignment.getTask().getTitle();

        // Excluir assignment
        taskAssignmentRepository.delete(assignment);

        // Notificar criança da remoção
        notificationService.create(childId, NotificationType.TASK_ASSIGNED,
                "Tarefa removida",
                "A tarefa '" + taskTitle + "' foi removida pelo responsável",
                null, null);
    }
}
