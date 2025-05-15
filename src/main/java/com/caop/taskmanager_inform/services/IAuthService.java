package com.caop.taskmanager_inform.services;

import com.caop.taskmanager_inform.dto.AuthRequest;
import com.caop.taskmanager_inform.dto.AuthResponse;

public interface IAuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse register(AuthRequest request);
}
