package com.example.book.service;

import com.example.book.dto.ReviewDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
import com.example.book.model.Review;
import com.example.book.model.User;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ReviewRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class ReviewService {
    private final ReviewRepository repository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;

    public ReviewService(ReviewRepository repository, UserRepository userRepository, ServiceRepository serviceRepository, BookingRepository bookingRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
    }

    public Review createReview(ReviewDto input, String email){
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));

        Booking booking = bookingRepository.findById(input.getBookingId()).orElseThrow(()-> new RuntimeException("Booking Not Found"));

        com.example.book.model.Service service = serviceRepository.findById(booking.getService().getId()).orElseThrow(()-> new RuntimeException("Service Not Found"));

        boolean reviewExists = repository.findByUserBooking(booking.getId(),owner.getId());

        if( LocalDate.now().isBefore(booking.getDate()) || LocalTime.now().isBefore(booking.getEnd())){
            System.out.println("Cannot Review Before Booking Date OR Time");
            throw new IllegalStateException("Only One Review Per User Per Booking");
        }


        if(reviewExists){
            System.out.println("review exists for booking and user");
            throw new IllegalStateException("Only One Review Per User Per Booking");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setService(service);
        review.setUser(owner);
        review.setRating(input.getRating());
        review.setComment(input.getComment());

        repository.save(review);
        System.out.println("saved");
        return review;
    }

    public Review updateReview(Long id, ReviewDto input, String email){
        Review review = repository.findById(id).orElseThrow(()-> new RuntimeException("Review Not Found"));
        if(review.getUpdatedAt() != null){
            throw new RuntimeException("Only One Update Per Review");
        }
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        Booking booking = bookingRepository.findById(input.getBookingId()).orElseThrow(()-> new RuntimeException("Booking Not Found"));
        com.example.book.model.Service service = serviceRepository.findById(booking.getService().getId()).orElseThrow(()-> new RuntimeException("Service Not Found"));

        review.setBooking(booking);
        review.setService(service);
        review.setUser(owner);
        review.setRating(input.getRating());
        review.setComment(input.getComment());
        repository.save(review);
        return review;

    }

    public ResponseEntity<?> deleteReview(Long id, String email){
        Review review = repository.findById(id).orElseThrow(()-> new RuntimeException("Review Not Found"));
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        if(!review.getUser().equals(owner)){
            throw new IllegalArgumentException("Only Reviewer Can Delete Review");
        }

        repository.delete(review);
        return ResponseEntity.ok("Review Deleted");
    }

}
