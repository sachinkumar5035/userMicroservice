package com.microservice.user.services.impl;

import com.microservice.user.entities.User;
import com.microservice.user.exceptions.ResourceNotFoundException;
import com.microservice.user.repositories.UserRepository;
import com.microservice.user.services.UserService;
import com.microservice.user.payload.SignUpRequest;
import com.microservice.user.payload.LoginRequest;
import com.microservice.user.payload.AuthResponse;
import com.microservice.user.payload.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.microservice.user.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User createUser(User user) {
        // generate random user id for this user
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        // ensure role is set
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        // encode password if provided
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found for the given id"));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user not found for the given email"));
    }

    @Override
    public User updateUser(String userId, User user) {
       User existingUser = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user not found for this id: "+userId));
       existingUser.setName(user.getName());
       existingUser.setEmail(user.getEmail());
       existingUser.setAbout(user.getAbout());
       // userId will remain same
       return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String userId) {
         User existingUser = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user is not available for the given id: "+userId));
         userRepository.delete(existingUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(signUpRequest.getEmail());
        if (existingUser.isPresent()) {
            return AuthResponse.builder()
                .message("Email already registered")
                .success(false)
                .build();
        }

        // Create new user
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setAbout(signUpRequest.getAbout());
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        // default role for new users
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        UserResponse userResp = new UserResponse(savedUser.getUserId(), savedUser.getName(), savedUser.getEmail(), savedUser.getAbout(), savedUser.getRole());
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
            .message("User registered successfully")
            .success(true)
            .user(userResp)
            .token(token)
            .build();
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (!userOptional.isPresent()) {
            return AuthResponse.builder()
                .message("Invalid email or password")
                .success(false)
                .build();
        }

        User user = userOptional.get();

        // Check if password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return AuthResponse.builder()
                .message("Invalid email or password")
                .success(false)
                .build();
        }

        UserResponse userResp = new UserResponse(user.getUserId(), user.getName(), user.getEmail(), user.getAbout(), user.getRole());
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
            .message("Login successful")
            .success(true)
            .user(userResp)
            .token(token)
            .build();
    }
}
