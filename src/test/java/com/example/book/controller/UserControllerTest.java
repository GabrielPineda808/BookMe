package com.example.book.controller;

import com.example.book.dto.UserDto;
import com.example.book.model.Location;
import com.example.book.model.Role;
import com.example.book.model.User;
import com.example.book.service.AuthenticationService;
import com.example.book.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirst_name("Test");
        user.setLast_name("User");
        user.setRole(Role.USER);
        user.setEnabled(true);

        Location location = new Location();
        location.setAddress("123 Main");
        location.setCity("City");
        location.setState("ST");
        location.setArea_code("12345");
        location.setCountry("USA");
        user.setLocation(location);
    }

    @Test
    @WithMockUser(roles = "USER")
    void myProfile_returnsUserDto() throws Exception {
        when(userService.findUserByUsername(user.getEmail())).thenReturn(user);

        mockMvc.perform(get("/user/profile").principal(() -> user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.first_name").value(user.getFirst_name()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_returnsUpdatedDto() throws Exception {
        UserDto dto = new UserDto();
        dto.setFirst_name("New");
        dto.setLast_name("Name");
        dto.setEmail(user.getEmail());
        dto.setLocation(user.getLocation());

        when(userService.updateUser(any(UserDto.class), eq(user.getEmail()))).thenReturn(user);

        mockMvc.perform(post("/user/update")
                .principal(() -> user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value(user.getFirst_name()))
                .andExpect(jsonPath("$.last_name").value(user.getLast_name()));
    }
}
