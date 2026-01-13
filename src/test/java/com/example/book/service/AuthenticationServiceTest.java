package com.example.book.service;

import com.example.book.dto.ChangePasswordDto;
import com.example.book.exception.PasswordsDoNotMatchException;
import com.example.book.model.Role;
import com.example.book.model.User;
import com.example.book.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    EmailService emailService;

    @InjectMocks
    AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("hashed");
        user.setFirst_name("Test");
        user.setLast_name("User");
        user.setRole(Role.USER);
        user.setEnabled(true);
    }

    @Test
    void changePassword_updatesWhenOldMatches() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("old");
        dto.setNewPassword("new");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("new-hash");

        authenticationService.changePassword(dto, user.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_throwsWhenOldDoesNotMatch() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("wrong");
        dto.setNewPassword("new");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getOldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(PasswordsDoNotMatchException.class,
                () -> authenticationService.changePassword(dto, user.getEmail()));
        verify(userRepository, never()).save(any());
    }
}
