package com.bookItNow.user.service;

import com.bookItNow.common.dto.UserDTO;
import com.bookItNow.common.dto.UserResponseDTO;
import com.bookItNow.common.dto.UserLoginDTO;
import com.bookItNow.user.feign.BookingClient;
import com.bookItNow.user.model.User;
import com.bookItNow.user.exception.UserNotFoundException;
import com.bookItNow.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final JWTService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final AuthenticationManager authenticationManager;

    private final BookingClient bookingClient;

    public UserServiceImpl(UserRepository userRepository, JWTService jwtService, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, BookingClient bookingClient) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.bookingClient = bookingClient;
    }

    /**
     * Creates a new user and saves it in the database.
     *
     * @param user The user object to be saved.
     * @return The saved user entity.
     */
    @Override
    public UserResponseDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());

        User savedUser = userRepository.save(user);
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
        Optional<User> userOpt = userRepository.findByUsername(username);

        return userOpt.map(user -> new UserResponseDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole())).orElseThrow(() -> new UserNotFoundException("User not found with this "+username ));
    }

    @Override
    public List<User> findAllUser()throws UserNotFoundException{
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
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    /**
     * Updates an existing user's details.
     *
     * @param user The updated user object.
     * @return The updated user entity.
     */
    @Override
    public UserResponseDTO updateUser(User user) throws UserNotFoundException {
        // Ensure the user exists before updating.
        if (!userRepository.existsById((user.getId()))) {
            throw new UserNotFoundException("Cannot update: User not found with ID: " + user.getId());
        }
        User savedUser = userRepository.save(user);

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
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Cannot delete: User not found with ID: " + userId);
        }
        userRepository.deleteById(Math.toIntExact(userId));
    }

    @Override
    public Map<String,String> verify(UserLoginDTO user) {
        User existingUser = userRepository.findByUsername(user.getUsername()).orElseThrow(()->new UserNotFoundException("User not found with username: " + user.getUsername()));
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated()){
            String accessToken = jwtService.generateToken(existingUser.getUsername(),existingUser.getRole());
            String refreshToken = refreshTokenService.createRefreshToken(existingUser.getUsername()).getToken();
            Map<String, String> map = new HashMap<>();

            map.put("access_token", accessToken);
            map.put("refresh_token", refreshToken);
            return map;
        }else{
            throw new UserNotFoundException("Invalid username or password");
        }


    }

    @Override
    public List<String> getUserBookings(int userId) {
        return bookingClient.getUserBookings(userId);
    }
}
