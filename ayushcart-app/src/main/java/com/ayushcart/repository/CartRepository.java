package com.ayushcart.repository;

import com.ayushcart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    /** "user.email" is navigated automatically: findBy + User + Email. */
    Optional<Cart> findByUserEmail(String email);
}
