package com.example.book.service;

import com.example.book.model.Role;
import com.example.book.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        // 32-byte key (base64) to satisfy HS256 minimum requirements
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "aGVsbG93b3JsZGhlbGxvd29ybGRoZWxsb3dvcmxkMTIzNDU2Nzg=");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3_600_000L);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("secret");
        user.setRole(Role.USER);
        user.setFirst_name("Test");
        user.setLast_name("User");
        user.setEnabled(true);
    }

    @Test
    void generateToken_roundTripsUsernameAndValidates() {
        String token = jwtService.generateToken(user);

        assertEquals(user.getEmail(), jwtService.extractUserName(token));
        assertTrue(jwtService.isTokenValid(token, user));
        assertTrue(jwtService.getExpirationTime() > 0);
    }

    @Test
    void isTokenValid_returnsFalseWhenExpired() {
        // Negative expiration produces already-expired token
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1L);
        String token = jwtService.generateToken(user);

        assertFalse(jwtService.isTokenValid(token, user));
    }
}
