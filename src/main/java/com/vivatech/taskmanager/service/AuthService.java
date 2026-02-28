package com.vivatech.taskmanager.service;

import com.vivatech.taskmanager.dto.*;

public interface AuthService {


    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
    void logout();


}