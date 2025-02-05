package com.bookItNow.service;

import com.bookItNow.model.User;
import com.bookItNow.exception.UserNotFoundException;
import com.bookItNow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Creates a new user and saves it in the database.
     *
     * @param user The user object to be saved.
     * @return The saved user entity.
     */
    @Override
    public User createUser(User user) {
        // Additional validations (e.g., check for unique username or email) can be added here.
        return userRepository.save(user);
    }

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the user if found, or empty if not found.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by their email.
     *
     * @param email The email to search for.
     * @return An Optional containing the user if found, or empty if not found.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to search for.
     * @return The user entity if found.
     * @throws UserNotFoundException If no user is found with the given ID.
     */
    @Override
    public User findById(Integer id) throws UserNotFoundException {
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
    public User updateUser(User user) {
        // Ensure the user exists before updating.
        if (!userRepository.existsById((user.getId()))) {
            throw new UserNotFoundException("Cannot update: User not found with ID: " + user.getId());
        }
        return userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId The ID of the user to delete.
     * @throws UserNotFoundException If no user is found with the given ID.
     */
    @Override
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Cannot delete: User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated()){
            return jwtService.generateToken(user.getUsername(),user.getRole());
        }

        return "fail";
    }
}
