package com.caop.taskmanager_inform.services;

import com.caop.taskmanager_inform.dto.TaskRequest;
import com.caop.taskmanager_inform.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITaskService {

    TaskResponse createTask(TaskRequest request);

    List<TaskResponse> getTasksByUserId(Integer userId);

    TaskResponse updateTask(Integer id, TaskRequest request);

    void deleteTask(Integer id);

    TaskResponse getTaskById(Integer id);

    Page<TaskResponse> searchTasks(String title, String description, String status, Pageable pageable);

}
