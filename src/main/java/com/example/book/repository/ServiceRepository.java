package com.example.book.repository;

import com.example.book.model.Service;
import com.example.book.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    // Find services by owner
    List<Service> findByUser(User user);
    
    // Find services by owner ID
    List<Service> findByUserId(Long userId);
    
    // Find service by handle (unique identifier)
    Optional<Service> findByHandle(String handle);
    
    // Check if handle exists (for validation)
    boolean existsByHandle(String handle);
    
    // Find services by location city
    @Query("SELECT s FROM Service s WHERE s.location.city = :city")
    List<Service> findByCity(@Param("city") String city);
    
    // Find services by location state
    @Query("SELECT s FROM Service s WHERE s.location.state = :state")
    List<Service> findByState(@Param("state") String state);
    
    // Find services with active bookings
    @Query("SELECT DISTINCT s FROM Service s JOIN s.bookings b WHERE s.user.id = :userId AND b.status IN ('PENDING', 'CONFIRMED')")
    List<Service> findServicesWithActiveBookings(@Param("userId") Long userId);
    
    // Count active bookings for a service
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.service.id = :serviceId AND b.status IN ('PENDING', 'CONFIRMED')")
    long countActiveBookings(@Param("serviceId") Long serviceId);
}
