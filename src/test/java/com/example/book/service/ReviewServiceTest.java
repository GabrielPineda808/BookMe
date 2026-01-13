package com.example.book.service;

import com.example.book.dto.ReviewDto;
import com.example.book.exception.ReviewOwnershipException;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.Review;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ReviewRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ServiceRepository serviceRepository;
    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    ReviewService reviewService;

    private User user;
    private Service service;
    private Booking booking;

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
        service.setUser(user);
        service.setInterval(60);
        service.setOpen(LocalTime.of(9, 0));
        service.setClose(LocalTime.of(17, 0));
        service.setEnabled(true);

        booking = new Booking();
        booking.setId(3L);
        booking.setService(service);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setDate(LocalDate.now().minusDays(1));
        booking.setStart(LocalTime.of(10, 0));
        booking.setEnd(LocalTime.of(11, 0));
    }

    @Test
    void createReview_succeedsWhenAfterBooking() {
        ReviewDto dto = new ReviewDto();
        dto.setBookingId(booking.getId());
        dto.setRating(5L);
        dto.setComment("Great");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(dto.getBookingId())).thenReturn(Optional.of(booking));
        when(serviceRepository.findById(service.getId())).thenReturn(Optional.of(service));
        when(reviewRepository.findByUserBooking(booking.getId(), user.getId())).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review review = reviewService.createReview(dto, user.getEmail());

        assertEquals(dto.getRating(), review.getRating());
        assertEquals(dto.getComment(), review.getComment());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void deleteReview_throwsWhenNotOwner() {
        Review review = new Review();
        review.setId(4L);
        review.setUser(user);

        User other = new User();
        other.setId(9L);
        other.setEmail("other@example.com");

        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(userRepository.findByEmail(other.getEmail())).thenReturn(Optional.of(other));

        assertThrows(ReviewOwnershipException.class,
                () -> reviewService.deleteReview(review.getId(), other.getEmail()));
    }
}
