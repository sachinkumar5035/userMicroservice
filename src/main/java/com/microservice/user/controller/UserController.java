package com.microservice.user.controller;

import com.microservice.user.entities.User;
import com.microservice.user.services.UserService;
import com.microservice.user.payload.SignUpRequest;
import com.microservice.user.payload.LoginRequest;
import com.microservice.user.payload.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // constructor injection
    UserController(UserService userService) {
        this.userService = userService;
    }

    // create a user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User user1 = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    // get user details (requires valid JWT and owner or admin)
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        Object principal = auth.getPrincipal();
        String requesterEmail = null;
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (principal instanceof UserDetails) {
            requesterEmail = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            requesterEmail = (String) principal;
        }

        // allow if admin
        if (isAdmin) {
            User user1 = userService.getUserById(userId);
            return ResponseEntity.ok(user1);
        }

        // otherwise ensure the requester owns the resource
        if (requesterEmail != null) {
            User requester = userService.getUserByEmail(requesterEmail);
            if (requester.getUserId().equals(userId)) {
                User user1 = userService.getUserById(userId);
                return ResponseEntity.ok(user1);
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    }

    // get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // update user
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @RequestBody User user) {
        User user1 = userService.updateUser(userId, user);
        return ResponseEntity.ok(user1);
    }

    // delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    // signup endpoint
    @PostMapping("/auth/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        AuthResponse response = userService.signUp(signUpRequest);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // login endpoint
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = userService.login(loginRequest);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
}
