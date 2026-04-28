package com.researchworkbench.service;

import com.researchworkbench.domain.AppUser;
import com.researchworkbench.dto.auth.AuthDtos;
import com.researchworkbench.repository.AppUserRepository;
import com.researchworkbench.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;

    public AuthService(
        AuthenticationManager authenticationManager,
        AppUserRepository appUserRepository,
        JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.appUserRepository = appUserRepository;
        this.jwtService = jwtService;
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        AppUser user = appUserRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(user);
        return new AuthDtos.AuthResponse(token, user.getEmail(), user.getDisplayName(), user.getRole().name());
    }
}
