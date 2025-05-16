package com.caop.taskmanager_inform.services;

import com.caop.taskmanager_inform.dto.TaskRequest;
import com.caop.taskmanager_inform.dto.TaskResponse;
import com.caop.taskmanager_inform.models.Task;
import com.caop.taskmanager_inform.models.TaskStatus;
import com.caop.taskmanager_inform.models.User;
import com.caop.taskmanager_inform.repositories.TaskRepository;
import com.caop.taskmanager_inform.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;



    public TaskService(TaskRepository taskRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public TaskResponse createTask(TaskRequest request) {
        User user = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new IllegalArgumentException("task.assigned_user_not_found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setCreatedAt(LocalDateTime.now());
        task.setAssignedTo(user);

        Task saved = taskRepository.save(task);

        TaskResponse response = mapToResponse(saved);
        return response;
    }

    @Override
    public List<TaskResponse> getTasksByUserId(Integer userId) {
        return taskRepository.findByAssignedToId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse updateTask(Integer id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("task.not_found"));

        User user = userRepository.findById(request.getAssignedUserId())
                .orElseThrow(() -> new IllegalArgumentException("task.assigned_user_not_found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setAssignedTo(user);

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("task.not_found");
        }
        taskRepository.deleteById(id);
    }

    @Override
    public TaskResponse getTaskById(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("task.not_found"));

        return mapToResponse(task);
    }

    @Override
    public Page<TaskResponse> searchTasks(String title, String description, String status, Pageable pageable) {
        Specification<Task> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }

        if (description != null && !description.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), TaskStatus.valueOf(status)));
        }

        return taskRepository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }


    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());
        response.setAssignedUserId(task.getAssignedTo().getId());
        response.setAssignedUserName(task.getAssignedTo().getName());
        return response;
    }

}
