package com.example.book.repository;

import com.example.book.model.BookingChangeRequest;
import com.example.book.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingChangeRequestRepository extends JpaRepository<BookingChangeRequest, Long> {
    
    List<BookingChangeRequest> findByBookingIdAndStatus(Long bookingId, BookingStatus status);
    
    List<BookingChangeRequest> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    @Query("SELECT bcr FROM BookingChangeRequest bcr WHERE bcr.booking.id = :bookingId AND bcr.status = :status AND bcr.expiresAt > :now")
    Optional<BookingChangeRequest> findActiveChangeRequest(@Param("bookingId") Long bookingId, 
                                                          @Param("status") BookingStatus status, 
                                                          @Param("now") LocalDateTime now);
    
    @Query("SELECT bcr FROM BookingChangeRequest bcr WHERE bcr.user.id = :userId AND bcr.status = :status AND bcr.expiresAt > :now")
    List<BookingChangeRequest> findActiveChangeRequestsForUser(@Param("userId") Long userId, 
                                                              @Param("status") BookingStatus status, 
                                                              @Param("now") LocalDateTime now);
}
