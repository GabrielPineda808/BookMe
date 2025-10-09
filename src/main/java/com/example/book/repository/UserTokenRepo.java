package com.example.book.repository;

import com.example.book.model.UserToken;
import com.example.book.model.TokenPurpose;
import com.example.book.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface UserTokenRepo extends JpaRepository<UserToken, Long> {
    List<UserToken> findByUserAndPurposeAndUsedFalseAndExpiresAtAfter(User user, TokenPurpose purpose, LocalDateTime now);
    // provide a cleanup query/add a method to find unused expired tokens for maintenance
}

