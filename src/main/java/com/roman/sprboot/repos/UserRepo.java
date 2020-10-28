package com.roman.sprboot.repos;

import com.roman.sprboot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String email);

    Optional<User> findById(Long id);

    void deleteById(Long id);

    void delete(User user);
}