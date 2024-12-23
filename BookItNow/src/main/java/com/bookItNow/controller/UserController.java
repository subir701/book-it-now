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
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SeatService seatService;

    /**
     * Register a new user.
     *
     * @param user The user object to register.
     * @return The created user entity.
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Login a user.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return Success or error message based on credentials.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) throws Exception {
        Optional<User> existingUser = userService.findByUsername(username);

        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if (!password.equals(existingUser.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        return ResponseEntity.ok("Login successful");
    }

    /**
     * Fetch a user by ID.
     *
     * @param id The ID of the user.
     * @return The user object if found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Update user details.
     *
     * @param id   The ID of the user to update.
     * @param user The updated user object.
     * @return The updated user entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    /**
     * Delete a user by ID.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Select a seat for an event.
     *
     * @param userId The ID of the user selecting the seat.
     * @param seatId The ID of the seat being selected.
     * @return A success message.
     */
    @PutMapping("/{userId}/seats/{seatId}/select")
    public ResponseEntity<?> selectSeat(@PathVariable Integer userId, @PathVariable Integer seatId) {
        seatService.selectSeats(userId, seatId);
        return ResponseEntity.ok("Seat " + seatId + " has been successfully selected by user " + userId);
    }
}
