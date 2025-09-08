package com.example.book.repository;

import com.example.book.model.Booking;
import com.example.book.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
    select (count(b) > 0)
    from Booking b
    where b.service.id = :serviceId
      and b.date = :date
      and b.bookingStatus in (com.example.book.model.BookingStatus.PENDING, com.example.book.model.BookingStatus.CONFIRMED)
      and b.start < :end
      and b.end   > :start
    """)
    boolean existsOverlapping(
            @Param("serviceId") Long serviceId,
            @Param("date") LocalDate date,
            @Param("start") LocalTime start,
            @Param("end") LocalTime end
    );

    @Query("""
    SELECT (count(b.id) > 0)
    FROM Booking b
    WHERE b.service.id = :serviceId
    AND b.user.id = :userId
    AND b.status = "CONFIRMED"
    AND b.booking_date > b.booking_date -1
    AND b.booking_end < :currentTime
            """)
    boolean bookingExists(@Param("serviceId") Long serviceId,@Param("userId") Long userId, @Param("currentTime") LocalTime currentTime);
}
