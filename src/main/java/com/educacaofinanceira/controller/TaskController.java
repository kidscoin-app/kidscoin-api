package com.educacaofinanceira.controller;

import com.educacaofinanceira.dto.request.CreateTaskRequest;
import com.educacaofinanceira.dto.request.RejectTaskRequest;
import com.educacaofinanceira.dto.response.TaskAssignmentResponse;
import com.educacaofinanceira.dto.response.TaskResponse;
import com.educacaofinanceira.model.User;
import com.educacaofinanceira.service.TaskService;
import com.educacaofinanceira.util.SecurityHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final SecurityHelper securityHelper;

    /**
     * Cria uma nova tarefa (apenas PARENT)
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        User parent = securityHelper.getAuthenticatedUser();
        TaskResponse task = taskService.createTask(request, parent);
        return ResponseEntity.ok(task);
    }

    /**
     * Lista tarefas
     * - PARENT: todas as assignments da família
     * - CHILD: apenas suas próprias assignments
     */
    @GetMapping
    public ResponseEntity<List<TaskAssignmentResponse>> getTasks() {
        User user = securityHelper.getAuthenticatedUser();
        List<TaskAssignmentResponse> tasks = taskService.getTasks(user);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Marca tarefa como concluída (apenas CHILD)
     */
    @PostMapping("/{assignmentId}/complete")
    public ResponseEntity<TaskAssignmentResponse> completeTask(@PathVariable UUID assignmentId) {
        User child = securityHelper.getAuthenticatedUser();
        TaskAssignmentResponse task = taskService.completeTask(assignmentId, child);
        return ResponseEntity.ok(task);
    }

    /**
     * Aprova tarefa (apenas PARENT)
     */
    @PostMapping("/{assignmentId}/approve")
    public ResponseEntity<TaskAssignmentResponse> approveTask(@PathVariable UUID assignmentId) {
        User parent = securityHelper.getAuthenticatedUser();
        TaskAssignmentResponse task = taskService.approveTask(assignmentId, parent);
        return ResponseEntity.ok(task);
    }

    /**
     * Rejeita tarefa (apenas PARENT)
     */
    @PostMapping("/{assignmentId}/reject")
    public ResponseEntity<TaskAssignmentResponse> rejectTask(
            @PathVariable UUID assignmentId,
            @Valid @RequestBody RejectTaskRequest request) {
        User parent = securityHelper.getAuthenticatedUser();
        TaskAssignmentResponse task = taskService.rejectTask(
                assignmentId, request.getRejectionReason(), parent);
        return ResponseEntity.ok(task);
    }

    /**
     * Tenta novamente uma tarefa rejeitada (apenas CHILD)
     * REJECTED → PENDING + limpa campos
     */
    @PutMapping("/assignments/{assignmentId}/retry")
    public ResponseEntity<TaskAssignmentResponse> retryTask(@PathVariable UUID assignmentId) {
        User child = securityHelper.getAuthenticatedUser();
        TaskAssignmentResponse task = taskService.retryTask(assignmentId, child);
        return ResponseEntity.ok(task);
    }

    /**
     * Exclui tarefa atribuída (apenas PARENT)
     * Só pode excluir tarefas com status PENDING ou REJECTED
     */
    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID assignmentId) {
        User parent = securityHelper.getAuthenticatedUser();
        taskService.deleteTaskAssignment(assignmentId, parent);
        return ResponseEntity.noContent().build();
    }
}
