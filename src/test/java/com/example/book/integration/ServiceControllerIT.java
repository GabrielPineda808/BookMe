package com.example.book.integration;

import com.example.book.model.Role;
import com.example.book.model.User;
import com.example.book.repository.UserRepository;
import com.example.book.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceControllerIT extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("owner@example.com");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setFirst_name("Owner");
        user.setLast_name("User");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now().minusDays(1));
        user.setUpdatedAt(LocalDateTime.now().minusHours(1));
        User saved = userRepository.save(user);
        token = jwtService.generateToken(saved);
    }

    @Test
    void createService_thenListServices() throws Exception {
        String payload = """
                {
                  "handle": "haircut1",
                  "service_name": "Haircut",
                  "desc": "Simple cut",
                  "open": "09:00",
                  "close": "17:00",
                  "interval": 60
                }
                """;

        mockMvc.perform(post("/service/create-service")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.handle").value("haircut1"))
                .andExpect(jsonPath("$.name").value("Haircut"));

        mockMvc.perform(get("/service")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].handle").value("haircut1"));
    }
}
