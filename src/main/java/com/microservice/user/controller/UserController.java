package com.microservice.user.controller;

import com.microservice.user.entities.User;
import com.microservice.user.services.UserService;
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
    @GetMapping("/{userID}")
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

    // delete user

}
