package com.bookItNow.service;

import com.bookItNow.entity.User;
import com.bookItNow.exception.UserNotFoundException;
import com.bookItNow.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserService {
    User createUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findById(Integer id) throws UserNotFoundException;
    User updateUser(User user);
    void deleteUser(Integer userId);
}
