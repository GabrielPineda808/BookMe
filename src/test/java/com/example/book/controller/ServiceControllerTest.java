package com.example.book.controller;

import com.example.book.dto.ServiceDto;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
import com.example.book.service.ServiceManagementService;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal") // MockBean marked for removal in newer Spring versions; safe for test wiring
    @MockBean
    private ServiceManagementService serviceService;

    private User owner;
    private Service service;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setRole(Role.USER);
        owner.setEnabled(true);

        service = new Service();
        service.setId(10L);
        service.setUser(owner);
        service.setHandle("cut");
        service.setService_name("Haircut");
        service.setOpen(LocalTime.of(9, 0));
        service.setClose(LocalTime.of(17, 0));
        service.setInterval(60);
        service.setReviews(new ArrayList<>());
        service.setCreatedAt(LocalDateTime.now().minusDays(1));
        service.setUpdatedAt(LocalDateTime.now().minusHours(1));
        service.setEnabled(true);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllServices_returnsList() throws Exception {
        when(serviceService.getAllServices()).thenReturn(List.of(service));

        mockMvc.perform(get("/service"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(service.getId()))
                .andExpect(jsonPath("$[0].handle").value(service.getHandle()))
                .andExpect(jsonPath("$[0].intervalMinutes").value(service.getInterval()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createService_returnsCreatedService() throws Exception {
        ServiceDto dto = new ServiceDto();
        dto.setHandle("newcut");
        dto.setService_name("Haircut");
        dto.setOpen(LocalTime.of(9, 0));
        dto.setClose(LocalTime.of(17, 0));
        dto.setInterval(60);

        when(serviceService.createService(any(ServiceDto.class), eq(owner.getEmail()))).thenReturn(service);

        mockMvc.perform(post("/service/create-service")
                .principal(() -> owner.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.handle").value(service.getHandle()))
                .andExpect(jsonPath("$.name").value(service.getService_name()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteService_returnsOk() throws Exception {
        mockMvc.perform(delete("/service/{id}/delete", service.getId())
                .principal(() -> owner.getEmail()))
                .andExpect(status().isOk());
    }
}
