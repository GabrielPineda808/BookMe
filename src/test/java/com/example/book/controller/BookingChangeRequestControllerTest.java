package com.example.book.controller;

import com.example.book.dto.BookingChangeRequestDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.model.BookingStatus;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
import com.example.book.service.BookingChangeRequestService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingChangeRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingChangeRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal") // Spring Boot marks MockBean for removal in newer versions; safe to suppress
                                 // for test context mocks
    @MockBean
    private BookingChangeRequestService bcrService;

    private User owner;
    private User customer;
    private Service service;
    private Booking booking;
    private BookingChangeRequest changeRequest;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setRole(Role.USER);
        owner.setEnabled(true);

        customer = new User();
        customer.setId(2L);
        customer.setEmail("customer@example.com");
        customer.setRole(Role.USER);
        customer.setEnabled(true);

        service = new Service();
        service.setId(3L);
        service.setUser(owner);
        service.setHandle("svc");
        service.setService_name("Cut");
        service.setInterval(60);
        service.setOpen(LocalTime.of(9, 0));
        service.setClose(LocalTime.of(17, 0));
        service.setCreatedAt(LocalDateTime.now().minusDays(1));
        service.setUpdatedAt(LocalDateTime.now().minusHours(1));
        service.setEnabled(true);

        booking = new Booking();
        booking.setId(4L);
        booking.setUser(customer);
        booking.setService(service);
        booking.setStatus(BookingStatus.PENDING);
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setStart(LocalTime.of(10, 0));
        booking.setEnd(LocalTime.of(11, 0));
        booking.setCreatedAt(LocalDateTime.now().minusDays(1));
        booking.setUpdatedAt(LocalDateTime.now().minusHours(1));

        changeRequest = new BookingChangeRequest();
        changeRequest.setId(9L);
        changeRequest.setBooking(booking);
        changeRequest.setUser(customer);
        changeRequest.setCurrent_date(booking.getDate());
        changeRequest.setCurrent_start(booking.getStart());
        changeRequest.setCurrent_end(booking.getEnd());
        changeRequest.setProposed_date(LocalDate.now().plusDays(2));
        changeRequest.setProposed_start(LocalTime.of(12, 0));
        changeRequest.setProposed_end(LocalTime.of(13, 0));
        changeRequest.setStatus(BookingStatus.PENDING);
        changeRequest.setReason("Need to move");
        changeRequest.setExpires_at(LocalDateTime.now().plusHours(2));
    }

    @Test
    @WithMockUser(roles = "USER")
    void proposeChangeAsService_returnsDto() throws Exception {
        BookingChangeRequestDto input = new BookingChangeRequestDto();
        input.setBooking_id(booking.getId());
        input.setProposed_date(changeRequest.getProposed_date().toString());
        input.setProposed_start(changeRequest.getProposed_start().toString());
        input.setProposed_end(changeRequest.getProposed_end().toString());
        input.setReason("Need to move");

        when(bcrService.proposeChangeAsService(eq(booking.getId()), any(BookingChangeRequestDto.class),
                eq(owner.getEmail())))
                .thenReturn(changeRequest);

        mockMvc.perform(post("/change-booking/{id}/service-propose", booking.getId())
                .principal(() -> owner.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booking_id").value(booking.getId()))
                .andExpect(jsonPath("$.proposed_date").value(changeRequest.getProposed_date().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void acceptProposal_updatesBooking() throws Exception {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bcrService.acceptProposal(changeRequest.getId(), customer.getEmail())).thenReturn(booking);

        mockMvc.perform(put("/change-booking/{id}/user-accept", changeRequest.getId())
                .principal(() -> customer.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getChangeRequest_returnsRequest() throws Exception {
        when(bcrService.getChangeRequestById(changeRequest.getId())).thenReturn(changeRequest);

        mockMvc.perform(get("/change-booking/{id}", changeRequest.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(changeRequest.getId()));
    }
}
