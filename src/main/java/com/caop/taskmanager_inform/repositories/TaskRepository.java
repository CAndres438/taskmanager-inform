package com.caop.taskmanager_inform.repositories;

import com.caop.taskmanager_inform.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer>, JpaSpecificationExecutor<Task>{
    List<Task> findByAssignedToId(Integer userId);
}
