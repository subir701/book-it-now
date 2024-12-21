package com.bookItNow.controller;

import com.bookItNow.entity.User;
import com.bookItNow.service.SeatService;
import com.bookItNow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SeatService seatService;

    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        // Directly save user without password encoding
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody String userName, @RequestBody String password) throws Exception {
        // Find user by username
        Optional<User> existingUser = userService.findByUsername(userName);

        if (existingUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Validate password (basic check for simplicity)
        if (!password.equals(existingUser.get().getPassword())) {
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

    @PutMapping("event/{userId}/selected/{seatId}")
    public ResponseEntity<?> selectedSeated(@PathVariable Integer userId, @PathVariable Integer seatId) throws RuntimeException {
        seatService.selectSeats(userId,seatId);
        return ResponseEntity.ok("Selected seat " + seatId);
    }


}
