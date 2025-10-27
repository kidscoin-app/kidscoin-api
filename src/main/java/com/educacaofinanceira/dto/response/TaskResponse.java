package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.Task;
import com.educacaofinanceira.model.enums.RecurrenceType;
import com.educacaofinanceira.model.enums.TaskCategory;
import com.educacaofinanceira.model.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private Integer coinValue;
    private Integer xpValue;
    private TaskCategory category;
    private TaskStatus status;
    private UUID familyId;
    private String createdByName;
    private LocalDateTime createdAt;

    // Campos de recorrência
    private Boolean isRecurring;
    private RecurrenceType recurrenceType;
    private String recurrenceDays;
    private LocalDate recurrenceEndDate;

    public static TaskResponse fromTask(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCoinValue(task.getCoinValue());
        response.setXpValue(task.getXpValue());
        response.setCategory(task.getCategory());
        response.setStatus(task.getStatus());
        response.setFamilyId(task.getFamily().getId());
        response.setCreatedByName(task.getCreatedBy().getFullName());
        response.setCreatedAt(task.getCreatedAt());

        // Campos de recorrência
        response.setIsRecurring(task.getIsRecurring());
        response.setRecurrenceType(task.getRecurrenceType());
        response.setRecurrenceDays(task.getRecurrenceDays());
        response.setRecurrenceEndDate(task.getRecurrenceEndDate());

        return response;
    }
}
