package com.example.book.controller;

import com.example.book.dto.BookingDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
import com.example.book.service.BookingService;
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

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private User user;
    private Service service;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(2L);
        user.setEmail("customer@example.com");
        user.setRole(Role.USER);
        user.setEnabled(true);

        service = new Service();
        service.setId(1L);
        service.setHandle("svc-handle");
        service.setService_name("Haircut");
        service.setUser(user);
        service.setInterval(60);
        service.setOpen(LocalTime.of(9, 0));
        service.setClose(LocalTime.of(17, 0));
        service.setCreatedAt(LocalDateTime.now().minusDays(1));
        service.setUpdatedAt(LocalDateTime.now().minusHours(1));
        service.setEnabled(true);

        booking = new Booking();
        booking.setId(5L);
        booking.setUser(user);
        booking.setService(service);
        booking.setStatus(BookingStatus.PENDING);
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setStart(LocalTime.of(10, 0));
        booking.setEnd(LocalTime.of(11, 0));
        booking.setCreatedAt(LocalDateTime.now().minusDays(1));
        booking.setUpdatedAt(LocalDateTime.now().minusHours(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void bookService_returnsBookingResponse() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setServiceId(service.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setDate(booking.getDate());
        dto.setNotes("note");

        when(bookingService.createBooking(any(BookingDto.class), eq(user.getEmail())))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .principal(() -> user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.serviceId").value(service.getId()))
                .andExpect(jsonPath("$.serviceHandle").value(service.getHandle()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyBookings_returnsList() throws Exception {
        when(bookingService.getBookingsByOwner(user.getEmail())).thenReturn(java.util.List.of(booking));

        mockMvc.perform(get("/bookings/my-bookings").principal(() -> user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(booking.getId()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void acceptBooking_updatesStatus() throws Exception {
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookingService.manageBooking(booking.getId(), user.getEmail(), "accept"))
                .thenReturn(booking);

        mockMvc.perform(put("/bookings/{id}/service-accept", booking.getId())
                .principal(() -> user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}
