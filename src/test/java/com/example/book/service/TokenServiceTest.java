package com.example.book.service;

import com.example.book.model.TokenPurpose;
import com.example.book.model.User;
import com.example.book.model.UserToken;
import com.example.book.repository.UserTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    UserTokenRepo tokenRepo;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
    }

    @Test
    void createTokenFor_persistsHashedTokenAndReturnsRaw() {
        when(passwordEncoder.encode(any())).thenReturn("hashed-token");
        when(tokenRepo.save(any(UserToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String raw = tokenService.createTokenFor(user, TokenPurpose.PASSWORD_RESET, Duration.ofMinutes(15));

        assertNotNull(raw);
        assertFalse(raw.isBlank());

        ArgumentCaptor<UserToken> captor = ArgumentCaptor.forClass(UserToken.class);
        verify(tokenRepo).save(captor.capture());
        UserToken saved = captor.getValue();

        assertEquals("hashed-token", saved.getTokenHash());
        assertEquals(TokenPurpose.PASSWORD_RESET, saved.getPurpose());
        assertFalse(saved.isUsed());
    }

    @Test
    void validateAndConsumeToken_marksTokenUsedWhenMatch() {
        String rawToken = "raw-token";
        UserToken token = new UserToken();
        token.setUser(user);
        token.setTokenHash("hashed");
        token.setPurpose(TokenPurpose.PASSWORD_RESET);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        token.setUsed(false);

        when(tokenRepo.findByUserAndPurposeAndUsedFalseAndExpiresAtAfter(any(), any(), any()))
                .thenReturn(List.of(token));
        when(passwordEncoder.matches(rawToken, token.getTokenHash())).thenReturn(true);
        when(tokenRepo.save(any(UserToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserToken result = tokenService.validateAndConsumeToken(user, TokenPurpose.PASSWORD_RESET, rawToken);

        assertTrue(result.isUsed());
        verify(tokenRepo).save(token);
    }

    @Test
    void validateAndConsumeToken_throwsWhenNoMatch() {
        when(tokenRepo.findByUserAndPurposeAndUsedFalseAndExpiresAtAfter(any(), any(), any()))
                .thenReturn(List.of());

        assertThrows(IllegalArgumentException.class,
                () -> tokenService.validateAndConsumeToken(user, TokenPurpose.PASSWORD_RESET, "bad"));
    }
}
