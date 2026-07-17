package com.microservice.user.services.impl;

import com.microservice.user.entities.User;
import com.microservice.user.exceptions.ResourceNotFoundException;
import com.microservice.user.repositories.UserRepository;
import com.microservice.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        // generate random user id for this user
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found for the given id"));
    }

    @Override
    public User updateUser(String userId, User user) {
        return null;
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public List<User> getAllUsers() {
        return List.of();
    }
}
