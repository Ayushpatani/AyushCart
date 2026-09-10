package com.ayushcart.controller;

import com.ayushcart.dto.AuthResponse;
import com.ayushcart.dto.LoginRequest;
import com.ayushcart.dto.RegisterRequest;
import com.ayushcart.dto.UserResponse;
import com.ayushcart.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Principal = the logged-in user (set by JwtAuthFilter); getName() is the email. */
    @GetMapping("/me")
    public UserResponse me(Principal principal) {
        return authService.me(principal.getName());
    }
}
