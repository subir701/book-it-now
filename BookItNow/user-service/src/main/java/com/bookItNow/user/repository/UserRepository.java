package com.bookItNow.user.repository;


import com.bookItNow.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    // Handy method to find a user by their username.
    // Spring Data JPA auto-generates the implementation based on this method's name!

    Optional<User> findByEmail(String email);

    boolean existsById(int id);
    // Similar to the one above, but this helps when you need to look up a user by their email.
    // Great for things like login or password recovery.

}
