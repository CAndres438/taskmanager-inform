package services;

import com.caop.taskmanager_inform.controllers.NotificationController;
import com.caop.taskmanager_inform.dto.TaskRequest;
import com.caop.taskmanager_inform.dto.TaskResponse;
import com.caop.taskmanager_inform.models.Task;
import com.caop.taskmanager_inform.models.TaskStatus;
import com.caop.taskmanager_inform.models.User;
import com.caop.taskmanager_inform.repositories.TaskRepository;
import com.caop.taskmanager_inform.repositories.UserRepository;
import com.caop.taskmanager_inform.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationController notificationController;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTask_shouldCreateTaskSuccessfully() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Description");
        request.setStatus(TaskStatus.PENDING);
        request.setAssignedUserId(1);

        User user = new User();
        user.setId(1);
        user.setName("Andres User");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArguments()[0]);

        TaskResponse result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals("New Task", result.getTitle());
        assertEquals("Andres User", result.getAssignedUserName());
        verify(notificationController).sendTaskAssignedNotification(eq(1), anyString());
    }

    @Test
    void createTask_shouldThrowException_whenAssignedUserNotFound() {
        TaskRequest request = new TaskRequest();
        request.setAssignedUserId(99);

        when(userRepository.findById(99)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(request);
        });

        assertEquals("task.assigned_user_not_found", ex.getMessage());
    }
}
