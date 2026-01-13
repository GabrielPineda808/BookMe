package com.example.book.controller;

import com.example.book.dto.ReviewDto;
import com.example.book.model.Booking;
import com.example.book.model.Review;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
import com.example.book.service.ReviewService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private User user;
    private Service service;
    private Booking booking;
    private Review review;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setFirst_name("Test");
        user.setLast_name("User");
        user.setRole(Role.USER);
        user.setEnabled(true);

        service = new Service();
        service.setId(2L);
        service.setService_name("Haircut");

        booking = new Booking();
        booking.setId(3L);
        booking.setService(service);
        booking.setUser(user);
        booking.setDate(LocalDate.now().minusDays(1));
        booking.setStart(LocalTime.of(10, 0));
        booking.setEnd(LocalTime.of(11, 0));
        booking.setCreatedAt(LocalDateTime.now().minusDays(2));
        booking.setUpdatedAt(LocalDateTime.now().minusDays(1));

        review = new Review();
        review.setId(5L);
        review.setUser(user);
        review.setService(service);
        review.setBooking(booking);
        review.setRating(5L);
        review.setComment("Great");
        review.setCreatedAt(LocalDateTime.now().minusHours(1));
        review.setUpdatedAt(LocalDateTime.now().minusMinutes(30));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createReview_returnsResponse() throws Exception {
        ReviewDto dto = new ReviewDto();
        dto.setBookingId(booking.getId());
        dto.setRating(5L);
        dto.setComment("Great");

        when(reviewService.createReview(any(ReviewDto.class), eq(user.getEmail()))).thenReturn(review);

        mockMvc.perform(post("/reviews/review")
                .principal(() -> user.getEmail())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.serviceId").value(service.getId()));
    }
}
