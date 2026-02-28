package com.vivatech.taskmanager.service.impl;

import com.vivatech.taskmanager.entity.RefreshToken;
import com.vivatech.taskmanager.entity.User;
import com.vivatech.taskmanager.exception.AppException;
import com.vivatech.taskmanager.exception.ErrorCode;
import com.vivatech.taskmanager.repository.RefreshTokenRepository;
import com.vivatech.taskmanager.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;


    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;


    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // Delete existing refresh token if present
        refreshTokenRepository.deleteByUser(user);

        // Create new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now()
                        .plusSeconds(refreshExpiration / 1000)) // ← LocalDateTime
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }


    @Override
    public RefreshToken verifyRefreshToken(String token) {

        // Find refresh token in DB
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.TOKEN_NOT_FOUND));

        // Check if refresh token is expired
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        return refreshToken;
    }




    @Override
    @Transactional
    public void deleteRefreshToken(User user) {

        // Delete refresh token on logout
        refreshTokenRepository.deleteByUser(user);
    }

}
