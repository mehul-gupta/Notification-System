package org.example.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.notification.dto.request.CreateUserRequest;
import org.example.notification.dto.response.UserResponse;
import org.example.notification.model.User;
import org.example.notification.repository.UserRepository;
import org.example.notification.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserResponse register(CreateUserRequest request) {

        User user = new User();

        user.setUserId(UUID.randomUUID().toString());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDeviceToken(request.getDeviceToken());
        user.setPreferredChannels(request.getPreferredChannels());
        user.setCreatedAt(LocalDateTime.now());
        repository.save(user);

        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getPreferredChannels());
    }
}