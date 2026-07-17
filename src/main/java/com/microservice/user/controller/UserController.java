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

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // constructor injection
    UserController(UserService userService){
        this.userService = userService;
    }

    // create a user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User user1 = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    // get user details
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserDetails(@PathVariable String userId){
        User user1 = userService.getUserById(userId);
        return ResponseEntity.ok(user1);
    }

    // get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // update user
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId,@RequestBody User user){
        User user1 = userService.updateUser(userId,user);
        return ResponseEntity.ok(user1);
    }

    // delete user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId){
         userService.deleteUser(userId);
         return ResponseEntity.ok("User deleted successfully");
    }

    // signup endpoint
    @PostMapping("/auth/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest signUpRequest){
        AuthResponse response = userService.signUp(signUpRequest);
        if(response.isSuccess()){
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // login endpoint
    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        AuthResponse response = userService.login(loginRequest);
        if(response.isSuccess()){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

}
