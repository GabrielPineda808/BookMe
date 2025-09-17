package com.example.book.repository;

import com.example.book.model.BookingChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public abstract class BookingChangeRequestRepository implements JpaRepository<BookingChangeRequest,Long> {
}
