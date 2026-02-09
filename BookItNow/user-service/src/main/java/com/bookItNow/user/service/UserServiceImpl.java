package com.bookItNow.user.service;

import com.bookItNow.common.dto.UserDTO;
import com.bookItNow.common.dto.UserResponseDTO;
import com.bookItNow.common.dto.UserLoginDTO;
import com.bookItNow.user.feign.BookingClient;
import com.bookItNow.user.model.MyUserDetails;
import com.bookItNow.user.model.User;
import com.bookItNow.user.exception.UserNotFoundException;
import com.bookItNow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final JWTService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    private final BookingClient bookingClient;


    /**
     * Creates a new user and saves it in the database.
     *
     * @param user The user object to be saved.
     * @return The saved user entity.
     */
    @Override
    public UserResponseDTO createUser(UserDTO userDTO) {
        log.info("Creating user: {}", userDTO.getUsername());

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        User savedUser = userRepository.save(user);

        log.info("User created successfully with ID: {}", savedUser.getId());

        return new UserResponseDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the user if found, or empty if not found.
     */
    @Override
    public UserResponseDTO findByUsername(String username) throws UserNotFoundException {
        log.info("Finding user by username: {}", username);

        Optional<User> userOpt = userRepository.findByUsername(username);

        return userOpt
                .map(user -> {

                    log.info("User found with username: {}", username);

                    return new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
                })
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UserNotFoundException("User not found with this "+username )
                            ;});
    }

    @Override
    public List<User> findAllUser()throws UserNotFoundException{
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Finds a user by their email.
     *
     * @param email The email to search for.
     * @return An Optional containing the user if found, or empty if not found.
     */
    @Override
    public UserResponseDTO findByEmail(String email) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        return userOpt.map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole())).orElseThrow(() -> new UserNotFoundException("User not found with this email "+ email));
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to search for.
     * @return The user entity if found.
     * @throws UserNotFoundException If no user is found with the given ID.
     */
    @Override
    public User findById(int id) throws UserNotFoundException {

        log.info("Finding user by ID: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Finding user by ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
    }

    /**
     * Updates an existing user's details.
     *
     * @param user The updated user object.
     * @return The updated user entity.
     */
    @Override
    public UserResponseDTO updateUser(User user) throws UserNotFoundException {
        log.info("Updating user with ID: {}", user.getId());

        // Ensure the user exists before updating.
        if (!userRepository.existsById((user.getId()))) {
            log.warn("Cannot update. User not found with ID: {}", user.getId());
            throw new UserNotFoundException("Cannot update: User not found with ID: " + user.getId());
        }
        User savedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", savedUser.getId());

        return new UserResponseDTO(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail(), savedUser.getRole());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to delete.
     * @throws UserNotFoundException If no user is found with the given ID.
     */
    @Override
    public void deleteUser(int userId) {
        log.info("Deleting user with ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("Cannot delete. User not found with ID: {}", userId);
            throw new UserNotFoundException("Cannot delete: User not found with ID: " + userId);
        }

        userRepository.deleteById(Math.toIntExact(userId));
        log.info("User deleted successfully with ID: {}", userId);
    }

    @Override
    public Map<String,String> verify(UserLoginDTO user) {
        log.info("Verifying login for user: {}", user.getUsername());

//        User existingUser = userRepository.findByUsername(user.getUsername())
//                .orElseThrow(()-> {
//                    log.warn("Login failed. User not found: {}", user.getUsername());
//                    return new UserNotFoundException("User not found with username: " + user.getUsername());
//                });

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        if(authentication.isAuthenticated()){

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            User existingUser = userDetails.getUser();

            String accessToken = jwtService.generateToken(existingUser.getUsername(),existingUser.getRole(), existingUser.getId());
            String refreshToken = refreshTokenService.createRefreshToken(existingUser.getUsername()).getToken();

            log.info("Login successful for user: {}", user.getUsername());

            Map<String, String> map = new HashMap<>();

            map.put("access_token", accessToken);
            map.put("refresh_token", refreshToken);
            return map;
        }else{
            log.warn("Login failed. Invalid credentials for user: {}", user.getUsername());
            throw new UserNotFoundException("Invalid username or password");
        }

    }

    /**
     * Gets all bookings for a specific user from Booking Service.
     */

    @Override
    public List<String> getUserBookings(int userId) {
        log.info("Fetching bookings for user ID: {}", userId);
        return bookingClient.getUserBookings(userId);
    }

//    Logout the user using userId

    @Override
    @Transactional
    public void logout(int userId) throws UserNotFoundException {
        log.info("Processing logout for user ID: {}",userId);

//        1. Directly deleted the refresh token using the UserId
        refreshTokenService.deleteRefereshTokenByUserId(userId);


    }
}
