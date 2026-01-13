package com.example.book.controller;

import com.example.book.dto.LoginUserDto;
import com.example.book.dto.RegisterUserDto;
import com.example.book.model.Role;
import com.example.book.model.User;
import com.example.book.service.AuthenticationService;
import com.example.book.service.EmailService;
import com.example.book.service.JwtService;
import com.example.book.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtService jwtService;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private EmailService emailService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private com.example.book.repository.UserRepository userRepository;

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
    void login_returnsTokenResponse() throws Exception {
        when(authenticationService.authenticate(any(LoginUserDto.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);

        String body = """
                {"email":"user@example.com","password":"secret"}
                """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void signup_returnsCreatedUserDto() throws Exception {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("new@example.com");
        dto.setPassword("secret");
        dto.setFirstName("New");
        dto.setLastName("User");

        User saved = new User();
        saved.setId(2L);
        saved.setEmail(dto.getEmail());
        saved.setFirst_name(dto.getFirstName());
        saved.setLast_name(dto.getLastName());
        saved.setRole(Role.USER);
        saved.setEnabled(true);

        when(authenticationService.signup(any(RegisterUserDto.class))).thenReturn(saved);

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(dto.getEmail()))
                .andExpect(jsonPath("$.first_name").value(dto.getFirstName()))
                .andExpect(jsonPath("$.last_name").value(dto.getLastName()));
    }
}
