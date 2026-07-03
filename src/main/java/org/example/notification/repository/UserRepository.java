package org.example.notification.repository;

import org.example.notification.model.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);
    Optional<User> findById(String userId);
}