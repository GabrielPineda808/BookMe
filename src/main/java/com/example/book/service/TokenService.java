package com.example.book.service;

import com.example.book.model.UserToken;
import com.example.book.model.TokenPurpose;
import com.example.book.model.User;
import com.example.book.repository.UserTokenRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class TokenService {
    private final UserTokenRepo tokenRepo;
    private final PasswordEncoder tokenHasher; // e.g. BCryptPasswordEncoder

    public TokenService(UserTokenRepo tokenRepo, PasswordEncoder tokenHasher) {
        this.tokenRepo = tokenRepo;
        this.tokenHasher = tokenHasher;
    }

    private static final SecureRandom secureRandom = new SecureRandom();

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional
    public String createTokenFor(User user, TokenPurpose purpose, Duration ttl) {
        String raw = generateRawToken();
        String hash = tokenHasher.encode(raw);

        UserToken token = new UserToken();
        token.setUser(user);
        token.setTokenHash(hash);
        token.setPurpose(purpose);
        token.setExpiresAt(LocalDateTime.now().plus(ttl));
        token.setUsed(false);
        tokenRepo.save(token);
        return raw;
    }

    @Transactional
    public UserToken validateAndConsumeToken(User user, TokenPurpose purpose, String rawToken) {
        List<UserToken> list = tokenRepo.findByUserAndPurposeAndUsedFalseAndExpiresAtAfter(user, purpose, LocalDateTime.now());
        for (UserToken t : list) {
            if (tokenHasher.matches(rawToken, t.getTokenHash())) {
                t.setUsed(true);
                tokenRepo.save(t);
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid or expired token");
    }

    // cleanup method to delete expired tokens (call from scheduled job)
    @Transactional
    public void purgeExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepo.findAll().stream()
                .filter(t -> t.getExpiresAt().isBefore(now) || t.isUsed())
                .forEach(tokenRepo::delete);
    }
}
