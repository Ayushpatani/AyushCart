package com.ayushcart.repository;

import com.ayushcart.entity.Role;
import com.ayushcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data generates the SQL for these methods from their names.
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);
}
