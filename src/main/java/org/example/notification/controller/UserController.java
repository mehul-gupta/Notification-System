package org.example.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.notification.dto.request.CreateUserRequest;
import org.example.notification.dto.response.UserResponse;
import org.example.notification.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody CreateUserRequest request) {
        return service.register(request);
    }
}