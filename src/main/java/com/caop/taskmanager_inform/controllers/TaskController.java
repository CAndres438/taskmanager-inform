package com.caop.taskmanager_inform.controllers;

import com.caop.taskmanager_inform.dto.TaskRequest;
import com.caop.taskmanager_inform.dto.TaskResponse;
import com.caop.taskmanager_inform.services.ITaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final ITaskService taskService;

    public TaskController(ITaskService taskService){this.taskService = taskService;}

    @PostMapping("/tasks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        logger.info("Task: Creating task with title: {}", request.getTitle());
        return ResponseEntity.ok(taskService.createTask(request));
    }

    @GetMapping("/tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        logger.info("Task: Searching task with filters - title: {}, description: {}, status: {}", title, description, status);
        return ResponseEntity.ok(taskService.searchTasks(title, description, status, pageable));
    }

    @PutMapping("/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> update(@PathVariable Integer id, @Valid @RequestBody TaskRequest request) {
        logger.info("Task: Updating task id: {}", id);
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/tasks/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        taskService.deleteTask(id);
        logger.info("Task: Deleting task id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<TaskResponse> getById(@PathVariable Integer id) {
        logger.info("Task: Fetching task by id: {}", id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/tasks/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TaskResponse>> getMyTasks(@RequestAttribute("userId") Integer userId) {
        logger.info("Task: Fetching tasks for userId: {}", userId);
        return ResponseEntity.ok(taskService.getTasksByUserId(userId));
    }


}
