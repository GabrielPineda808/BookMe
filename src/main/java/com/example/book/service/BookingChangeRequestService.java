package com.example.book.service;

import com.example.book.dto.BookingChangeRequestDto;
import com.example.book.model.Booking;
import com.example.book.model.BookingChangeRequest;
import com.example.book.model.BookingStatus;
import com.example.book.model.User;
import com.example.book.repository.BookingChangeRequestRepository;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Service
public class BookingChangeRequestService {
    private final BookingChangeRequestRepository bcrr;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public BookingChangeRequestService(BookingChangeRequestRepository bcrr, BookingRepository bookingRepository, BookingService bookingService, ServiceRepository serviceRepository, UserRepository userRepository) {
        this.bcrr = bcrr;
        this.bookingRepository = bookingRepository;
        this.bookingService = bookingService;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingChangeRequest proposeChangeAsService(Long id, BookingChangeRequestDto input, String email){
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        Booking booking = bookingRepository.findById(id).orElseThrow(()-> new RuntimeException("Booking Not Found"));
        com.example.book.model.Service service = serviceRepository.findById(booking.getService().getId()).orElseThrow(()-> new RuntimeException("Service Not Found"));

        if(!Objects.equals(owner.getId(), service.getUser().getId())){
            throw new RuntimeException("Service Owner By User");
        }
        try {
            bookingService.bookingChecks(booking, owner, service);
        }catch (Exception e){
            throw new RuntimeException("Error With Booking");
        }

        BookingChangeRequest bcr = new BookingChangeRequest();
        bcr.setBooking(booking);
        bcr.setUser(booking.getUser());
        bcr.setCurrent_date(booking.getDate());
        bcr.setCurrent_start(booking.getStart());
        bcr.setCurrent_end(booking.getEnd());
        bcr.setProposed_date(LocalDate.parse(input.getProposed_date()));
        bcr.setProposed_start(LocalTime.parse(input.getProposed_start()));
        bcr.setProposed_end(LocalTime.parse(input.getProposed_end()));
        bcr.setStatus(BookingStatus.PENDING);
        bcr.setReason(input.getReason());
        bcr.setExpires_at(LocalDateTime.now().plusDays(1));

        bcrr.save(bcr);

        return bcr;
    }

    @Transactional
    public Booking acceptProposal(Long id, String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        BookingChangeRequest changeRequest = bcrr.findById(id).orElseThrow(()-> new RuntimeException("Change Request Not Found"));
        Booking booking = changeRequest.getBooking();

        // Verify the user is the booking owner (customer)
        if(!Objects.equals(user.getId(), booking.getUser().getId())){
            throw new RuntimeException("Only the booking owner can accept change proposals");
        }

        // Verify the change request is still pending and not expired
        if(changeRequest.getStatus() != BookingStatus.PENDING){
            throw new RuntimeException("Change request is no longer pending");
        }

        if(changeRequest.getExpires_at() != null && changeRequest.getExpires_at().isBefore(LocalDateTime.now())){
            changeRequest.setStatus(BookingStatus.EXPIRED);
            bcrr.save(changeRequest);
            throw new RuntimeException("Change request has expired");
        }

        // Update the booking with the proposed times
        booking.setDate(changeRequest.getProposed_date());
        booking.setStart(changeRequest.getProposed_start());
        booking.setEnd(changeRequest.getProposed_end());
        booking.setStatus(BookingStatus.CONFIRMED);

        // Update the change request status
        changeRequest.setStatus(BookingStatus.CONFIRMED);
        changeRequest.setResponse_reason("Accepted by customer");

        // Save both entities
        bookingRepository.save(booking);
        bcrr.save(changeRequest);

        return booking;
    }

    @Transactional
    public Booking declineProposal(Long id, String email, String reason){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        BookingChangeRequest changeRequest = bcrr.findById(id).orElseThrow(()-> new RuntimeException("Change Request Not Found"));
        Booking booking = changeRequest.getBooking();

        // Verify the user is the booking owner (customer)
        if(!Objects.equals(user.getId(), booking.getUser().getId())){
            throw new RuntimeException("Only the booking owner can decline change proposals");
        }

        // Verify the change request is still pending
        if(changeRequest.getStatus() != BookingStatus.PENDING){
            throw new RuntimeException("Change request is no longer pending");
        }

        // Update the change request status
        changeRequest.setStatus(BookingStatus.DECLINED);
        changeRequest.setResponse_reason(reason != null ? reason : "Declined by customer");

        bcrr.save(changeRequest);

        return booking;
    }

    public List<BookingChangeRequest> getPendingChangeRequestsForUser(String email){
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        return bcrr.findActiveChangeRequestsForUser(user.getId(), BookingStatus.PENDING, LocalDateTime.now());
    }

    public List<BookingChangeRequest> getPendingChangeRequestsForServiceOwner(String email){
        User owner = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        // Get all pending change requests for services owned by this user
        return bcrr.findAll().stream()
                .filter(bcr -> bcr.getStatus() == BookingStatus.PENDING)
                .filter(bcr -> Objects.equals(bcr.getBooking().getService().getUser().getId(), owner.getId()))
                .filter(bcr -> bcr.getExpires_at() == null || bcr.getExpires_at().isAfter(LocalDateTime.now()))
                .toList();
    }

    @Transactional
    public void expireOldChangeRequests(){
        List<BookingChangeRequest> expiredRequests = bcrr.findAll().stream()
                .filter(bcr -> bcr.getStatus() == BookingStatus.PENDING)
                .filter(bcr -> bcr.getExpires_at() != null && bcr.getExpires_at().isBefore(LocalDateTime.now()))
                .toList();
        
        expiredRequests.forEach(bcr -> bcr.setStatus(BookingStatus.EXPIRED));
        bcrr.saveAll(expiredRequests);
    }

    public BookingChangeRequest getChangeRequestById(Long id){
        return bcrr.findById(id).orElseThrow(()-> new RuntimeException("Change Request Not Found"));
    }


}
