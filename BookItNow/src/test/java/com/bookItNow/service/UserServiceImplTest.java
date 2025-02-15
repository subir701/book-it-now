package com.bookItNow.service;

import com.bookItNow.exception.UserNotFoundException;
import com.bookItNow.model.User;
import com.bookItNow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testuser");

        when(userRepository.save(user)).thenReturn(user);

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals("testuser", createdUser.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testFindByUsername_Success() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUsername(username);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void testFindById_UserFound() throws UserNotFoundException {
        // Arrange
        int userId = 1;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testFindById_UserNotFound() {
        // Arrange
        int userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("updateduser");

        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User updatedUser = userService.updateUser(user);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getUsername());
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_UserNotFound() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.existsById(user.getId())).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(user));
        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, never()).save(user);
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        int userId = 1;

        when(userRepository.existsById(userId)).thenReturn(true);

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        // Arrange
        int userId = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }

//    @Test
//    void testVerify_Success() {
//        // Arrange
//        User user = new User();
//        user.setUsername("testuser");
//        user.setPassword("password");
//
//        Authentication authentication = mock(Authentication.class);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        when(authentication.isAuthenticated()).thenReturn(true);
//        when(jwtService.generateToken(user.getUsername(),user.getRole())).thenReturn("jwt-token");
//
//        // Act
//        String token = userService.verify(user);
//
//        // Assert
//        assertNotNull(token);
//        assertEquals("jwt-token", token);
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtService, times(1)).generateToken(user.getUsername(),user.getRole());
//    }
//
//    @Test
//    void testVerify_Fail() {
//        // Arrange
//        User user = new User();
//        user.setUsername("testuser");
//        user.setPassword("password");
//
//        Authentication authentication = mock(Authentication.class);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        when(authentication.isAuthenticated()).thenReturn(false);
//
//        // Act
//        String token = userService.verify(user);
//
//        // Assert
//        assertNotNull(token);
//        assertEquals("fail", token);
//        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
//        verify(jwtService, never()).generateToken(user.getUsername(), user.getRole());
//    }
}
