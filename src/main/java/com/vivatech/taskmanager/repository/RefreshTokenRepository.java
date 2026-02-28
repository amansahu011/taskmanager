package com.vivatech.taskmanager.repository;


import com.vivatech.taskmanager.entity.RefreshToken;
import com.vivatech.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
