package com.example.book.repository;

import com.example.book.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
            SELECT (count(r.id) > 0)
            FROM Review r
            WHERE r.booking.id = :bookingId
            AND r.user.id = :userId
            """)
    boolean findByUserBooking(@Param("bookingId") Long bookingId, @Param("userId") Long userId);
}
