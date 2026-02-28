package com.vivatech.taskmanager.service;

import com.vivatech.taskmanager.entity.RefreshToken;
import com.vivatech.taskmanager.entity.User;

public interface RefreshTokenService {


    RefreshToken createRefreshToken(User user);
    RefreshToken verifyRefreshToken(String token);
    void deleteRefreshToken(User user);

}