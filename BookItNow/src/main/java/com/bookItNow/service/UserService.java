package com.bookItNow.service;

import com.bookItNow.dto.UserDTO;
import com.bookItNow.model.User;
import com.bookItNow.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserService {
    User createUser(User user);
    Optional<User> findByUsername(String username);
    public List<User> findAllUser()throws UserNotFoundException;
    Optional<User> findByEmail(String email);
    User findById(Integer id) throws UserNotFoundException;
    User updateUser(User user);
    void deleteUser(Integer userId);
    Map<String, String> verify(UserDTO user);
}
