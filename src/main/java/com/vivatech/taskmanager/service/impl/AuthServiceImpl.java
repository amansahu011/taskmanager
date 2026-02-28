package com.vivatech.taskmanager.service.impl;

import com.vivatech.taskmanager.dto.*;
import com.vivatech.taskmanager.entity.RefreshToken;
import com.vivatech.taskmanager.entity.User;
import com.vivatech.taskmanager.exception.AppException;
import com.vivatech.taskmanager.exception.ErrorCode;
import com.vivatech.taskmanager.repository.UserRepository;
import com.vivatech.taskmanager.security.JwtService;
import com.vivatech.taskmanager.service.AuthService;
import com.vivatech.taskmanager.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;



    //logic for register.
    @Override
    public AuthResponse register(RegisterRequest request) {

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Build and save new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        // Generate access token
        String accessToken = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .message("User registered successfully")
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }


    //logic for login
    @Override
    public AuthResponse login(LoginRequest request) {

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Generate new access token
        String accessToken = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        // Generate new refresh token (old one will be deleted)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .message("Login successful")
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }



    //logic for refresh-token
    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        // Verify refresh token and get user
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(
                request.getRefreshToken()
        );

        User user = refreshToken.getUser();

        // Generate new access token
        String newAccessToken = jwtService.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new RefreshTokenResponse(newAccessToken, refreshToken.getToken());
    }


    //logic for logout api
    @Override
    public void logout() {

        // Get logged out user from SecurityContext
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        // Delete refresh token from DB
        refreshTokenService.deleteRefreshToken(user);
    }

}
