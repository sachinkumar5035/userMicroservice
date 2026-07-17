package com.microservice.user.services;


import com.microservice.user.entities.User;
import com.microservice.user.payload.SignUpRequest;
import com.microservice.user.payload.LoginRequest;
import com.microservice.user.payload.AuthResponse;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(String userId);
    User getUserByEmail(String email);
    User updateUser(String userId, User user);
    void deleteUser(String userId);
    List<User> getAllUsers();
    AuthResponse signUp(SignUpRequest signUpRequest);
    AuthResponse login(LoginRequest loginRequest);
}
