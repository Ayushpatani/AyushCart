package com.ayushcart.service;

import com.ayushcart.dto.AuthResponse;
import com.ayushcart.dto.LoginRequest;
import com.ayushcart.dto.RegisterRequest;
import com.ayushcart.dto.UserResponse;
import com.ayushcart.entity.Role;
import com.ayushcart.entity.User;
import com.ayushcart.exception.ConflictException;
import com.ayushcart.exception.ResourceNotFoundException;
import com.ayushcart.repository.UserRepository;
import com.ayushcart.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = userRepository.save(new User(request.fullName().trim(), email,
                passwordEncoder.encode(request.password()), Role.CUSTOMER));
        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());
        // Throws BadCredentialsException (-> 401) if the email or password is wrong
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse me(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user.getEmail(), user.getRole()), UserResponse.from(user));
    }

    /** Emails are stored lowercase so "Adam@Mail.com" and "adam@mail.com" are the same account. */
    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
