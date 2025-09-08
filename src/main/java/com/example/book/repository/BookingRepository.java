package com.example.book.repository;

import com.example.book.model.Booking;
import com.example.book.model.BookingStatus;
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
      and b.status in (com.example.book.model.BookingStatus.PENDING, com.example.book.model.BookingStatus.CONFIRMED)
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
select (count(b) > 0) from Booking b
where b.service.id = :serviceId
  and b.user.id = :userId
  and b.status = :status
  and b.date <= :booking_date
  and b.end <= :booking_end
""")
    boolean bookingExists(@Param("serviceId") Long serviceId,
                          @Param("userId") Long userId,
                          @Param("booking_date") LocalDate booking_date,
                          @Param("booking_end") LocalTime booking_end,
                          @Param("status")BookingStatus status);

}
