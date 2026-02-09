package com.bookItNow.user.service;

import com.bookItNow.common.dto.UserDTO;
import com.bookItNow.common.dto.UserResponseDTO;
import com.bookItNow.common.dto.UserLoginDTO;
import com.bookItNow.user.model.User;
import com.bookItNow.user.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserService {
    UserResponseDTO createUser(UserDTO userDTO);
    UserResponseDTO findByUsername(String username) throws UserNotFoundException;
    List<User> findAllUser()throws UserNotFoundException;
    UserResponseDTO findByEmail(String email)throws UserNotFoundException;
    User findById(int id) throws UserNotFoundException;
    UserResponseDTO updateUser(User user)throws UserNotFoundException;
    void deleteUser(int userId)throws UserNotFoundException;
    Map<String, String> verify(UserLoginDTO user);
    List<String> getUserBookings(int userId);

    void logout(int userId)throws UserNotFoundException;
}
