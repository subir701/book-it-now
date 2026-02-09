package com.bookItNow.user.service;

import com.bookItNow.common.dto.UserDTO;
import com.bookItNow.common.dto.UserLoginDTO;
import com.bookItNow.common.dto.UserResponseDTO;
import com.bookItNow.user.exception.UserNotFoundException;
import com.bookItNow.user.feign.BookingClient;
import com.bookItNow.user.model.MyUserDetails;
import com.bookItNow.user.model.RefreshToken;
import com.bookItNow.user.model.User;
import com.bookItNow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private JWTService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BookingClient client;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ShouldSaveUserAndResponseDTO() {
        UserDTO userDTO = new UserDTO("john","password","john@gmail.com","USER");

        User userToSave = new User();
        userToSave.setUsername("john");
        userToSave.setPassword("password");
        userToSave.setEmail("john@gmail.com");
        userToSave.setRole("USER");

        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("john");
        savedUser.setPassword("password");
        savedUser.setEmail("john@gmail.com");
        savedUser.setRole("USER");

        when(userRepo.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO responseDTO = userService.createUser(userDTO);

        assertEquals(1,responseDTO.getId());
        assertEquals("john",responseDTO.getUsername());
        assertEquals("john@gmail.com",responseDTO.getEmail());
        assertEquals("USER",responseDTO.getRole());

        verify(userRepo,times(1)).save(any(User.class));
    }

    @Test
    void findByUsername_ShouldReturnUserResponseDTO_WhenUserExists() {
        User user = new User();
        user.setId(1);
        user.setUsername("john");
        user.setPassword("password");
        user.setEmail("john@gmail.com");
        user.setRole("USER");

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));

        UserResponseDTO responseDTO = userService.findByUsername("john");

        assertEquals("john",responseDTO.getUsername());
        assertEquals(1,responseDTO.getId());
        assertEquals("john@gmail.com",responseDTO.getEmail());
        assertEquals("USER",responseDTO.getRole());

    }

    @Test
    void findByUsername_ShouldThrowUserNotFoundException_WhenUserDoesNotExist(){
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class,() -> userService.findByUsername("ghost"));
        assertEquals("User not found with this ghost",exception.getMessage());
    }

    @Test
    void findAllUser_ShouldReturnListOfUsers() {
        User u1 = new User();
        u1.setId(1);
        u1.setUsername("john");

        User u2 = new User();
        u2.setId(2);
        u2.setUsername("jane");

        when(userRepo.findAll()).thenReturn(Arrays.asList(u1,u2));

        List<User> list = userService.findAllUser();

        assertEquals(2,list.size());
        assertEquals(1,list.get(0).getId());
        assertEquals("john",list.get(0).getUsername());

        verify(userRepo).findAll();
    }

    @Test
    void findByEmail() {
    }

    @Test
    void findById() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void verify_ShouldReturnTokens_WhenCredentialsAreValid() {
        // 1. Setup your data
        UserLoginDTO loginDTO = new UserLoginDTO("john", "password");
        User userEntity = new User();
        userEntity.setId(1);
        userEntity.setUsername("john");
        userEntity.setRole("USER");

        // 2. Create the MyUserDetails object that the code expects
        MyUserDetails userDetails = new MyUserDetails(userEntity);

        // 3. Mock the Authentication object
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);

        // CRITICAL FIX: Tell the mock to return your userDetails
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // 4. Mock the JWT and Refresh Token services
        // Use anyInt() or the correct ID (1)
        when(jwtService.generateToken(eq("john"), eq("USER"), anyInt())).thenReturn("access-token");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        when(refreshTokenService.createRefreshToken("john")).thenReturn(refreshToken);

        // 5. Execute
        Map<String, String> result = userService.verify(loginDTO);

        // 6. Verify
        assertEquals("access-token", result.get("access_token"));
        assertEquals("refresh-token", result.get("refresh_token"));
    }

    @Test
    void getUserBookings() {
    }
}