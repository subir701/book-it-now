package com.bookItNow.user.controller;

import com.bookItNow.common.dto.UserDTO;
import com.bookItNow.common.dto.UserResponseDTO;
import com.bookItNow.common.dto.UserLoginDTO;
import com.bookItNow.user.model.MyUserDetails;
import com.bookItNow.user.model.User;
import com.bookItNow.user.service.UserService;
import com.bookItNow.user.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookitnow/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder ;

    public UserController(UserService userService) {
        this.userService = userService;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    }

    /**
     * Login a user.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return Success or error message based on credentials.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginDTO user, HttpServletResponse response) throws Exception {
        Map<String, String> token = userService.verify(user);

        log.info("User '{}' logged in successfully", user.getUsername());

        System.out.println(token.get("access_token"));

        CookieUtil.addCookie(response,"accessToken",token.get("access_token"),60000);
        CookieUtil.addCookie(response,"refreshToken",token.get("refresh_token"),60000);

        return ResponseEntity.ok("Logged In");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(Authentication authentication){

//        Get your custom UserDetails from the context
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

//        Pass the ID directly to the service
        userService.logout(userDetails.getId());

        return ResponseEntity.ok("Successfully logged out");
    }




    /**
     * Register a new user.
     *
     * @param user The user object to register.
     * @return The created user entity.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserDTO user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserResponseDTO createdUser = userService.createUser(user);

        log.info("New user '{}' registered successfully", createdUser.getUsername());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Fetch a user by ID.
     *
     * @param id The ID of the user.
     * @return The user object if found.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {

        return ResponseEntity.ok(userService.findById(id));
    }

    /**
     * Update user details.
     *
     * @param id   The ID of the user to update.
     * @param user The updated user object.
     * @return The updated user entity.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUserDetails(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        log.info("Updating user with ID: {}", id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    /**
     * Delete a user by ID.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Fetching all users");
        return new ResponseEntity<>(userService.findAllUser(), HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user by username: {}", username);
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping("/{userId}/bookings")
    public ResponseEntity<List<String>> getUserBookings(@PathVariable int userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        return ResponseEntity.ok(userService.getUserBookings(userId));
    }

}
