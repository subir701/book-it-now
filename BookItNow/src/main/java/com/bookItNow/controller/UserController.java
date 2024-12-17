package com.bookItNow.controller;

import com.bookItNow.entity.User;
import com.bookItNow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Directly save user without password encoding
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        // Find user by username
        Optional<User> existingUser = userService.findByUsername(user.getUsername());

        if (existingUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Validate password (basic check for simplicity)
        if (!user.getPassword().equals(existingUser.get().getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // Return success message instead of token
        return ResponseEntity.ok("Login successful");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
