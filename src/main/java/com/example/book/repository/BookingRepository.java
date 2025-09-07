package com.example.book.repository;

import com.example.book.model.Booking;
import com.example.book.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, User> {
}
