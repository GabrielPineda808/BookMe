package com.example.book.service;

import com.example.book.dto.ReviewDto;
import com.example.book.model.Review;
import com.example.book.model.User;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ReviewRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import org.springframework.stereotype.Service;

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
        com.example.book.model.Service service = serviceRepository.findById(input.getServiceId()).orElseThrow(()-> new RuntimeException("Service Not Found"));

        boolean overlaps = bookingRepository.bookingExists(
                service.getId(), owner.getId(), LocalTime.now()) ;

        if (!overlaps) {
            throw new IllegalStateException("Time slot overlaps an existing booking.");
        }

        Review review = new Review();
        review.setService(service);
        review.setUser(owner);
        review.setRating(input.getRating());
        review.setComment(input.getComment());

        repository.save(review);

        return review;
    }
}
