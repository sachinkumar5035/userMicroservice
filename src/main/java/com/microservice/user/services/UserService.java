package com.microservice.user.services;


import com.microservice.user.entities.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(String userId);
    User updateUser(String userId, User user);
    void deleteUser(String userId);
    List<User> getAllUsers();
}
