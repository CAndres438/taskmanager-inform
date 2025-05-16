package com.caop.taskmanager_inform.dto;

import com.caop.taskmanager_inform.models.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskRequest {
    @NotBlank(message = "task.title_required")
    private String title;

    private String description;

    @NotNull(message = "task.status_required")
    private TaskStatus status;

    @NotNull(message = "task.assigned_user_required")
    private Integer assignedUserId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Integer assignedUserId) {
        this.assignedUserId = assignedUserId;
    }
}
