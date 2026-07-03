package org.example.notification.service;

import org.example.notification.dto.request.CreateUserRequest;
import org.example.notification.dto.response.UserResponse;

public interface UserService {
    UserResponse register(CreateUserRequest request);
}