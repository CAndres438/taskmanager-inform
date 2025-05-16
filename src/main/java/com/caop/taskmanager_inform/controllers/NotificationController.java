package com.caop.taskmanager_inform.controllers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendTaskAssignedNotification(Integer userId, String message) {
        String destination = "/topic/user/" + userId;
        messagingTemplate.convertAndSend(destination, message);
    }
}
