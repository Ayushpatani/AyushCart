package com.ayushcart.security;

import com.ayushcart.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Tells Spring Security how to find a user by "username" (for us, the email). */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user with email " + email));
        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name()) // becomes authority "ROLE_ADMIN" / "ROLE_CUSTOMER"
                .build();
    }
}
