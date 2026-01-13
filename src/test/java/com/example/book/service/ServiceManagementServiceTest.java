package com.example.book.service;

import com.example.book.dto.ServiceDto;
import com.example.book.exception.ActiveBookingsException;
import com.example.book.exception.ServiceHandleExistsException;
import com.example.book.model.Role;
import com.example.book.model.Service;
import com.example.book.model.User;
import com.example.book.repository.BookingChangeRequestRepository;
import com.example.book.repository.BookingRepository;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceManagementServiceTest {

    @Mock
    ServiceRepository serviceRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    BookingChangeRequestRepository bookingChangeRequestRepository;

    @InjectMocks
    ServiceManagementService serviceService;

    private User owner;
    private Service existingService;
    private ServiceDto updateDto;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setRole(Role.USER);
        owner.setEnabled(true);

        existingService = new Service();
        existingService.setId(10L);
        existingService.setUser(owner);
        existingService.setHandle("old-handle");
        existingService.setService_name("Haircut");
        existingService.setOpen(LocalTime.of(9, 0));
        existingService.setClose(LocalTime.of(17, 0));
        existingService.setInterval(60);
        existingService.setEnabled(true);

        updateDto = new ServiceDto();
        updateDto.setHandle("new-handle");
        updateDto.setService_name("Haircut");
        updateDto.setOpen(LocalTime.of(9, 0));
        updateDto.setClose(LocalTime.of(17, 0));
        updateDto.setInterval(60);
    }

    @Test
    void createService_throwsWhenHandleExists() {
        ServiceDto input = updateDto;

        when(userRepository.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(serviceRepository.existsByHandle(input.getHandle())).thenReturn(true);

        assertThrows(ServiceHandleExistsException.class,
                () -> serviceService.createService(input, owner.getEmail()));
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void deleteService_preventsDeletionWhenActiveBookingsAndNotForced() {
        when(userRepository.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(serviceRepository.findById(existingService.getId())).thenReturn(Optional.of(existingService));
        when(serviceRepository.countActiveBookings(existingService.getId())).thenReturn(2L);

        assertThrows(ActiveBookingsException.class,
                () -> serviceService.deleteService(existingService.getId(), owner.getEmail(), false));
        verify(serviceRepository, never()).delete(any());
    }

    @Test
    void updateService_throwsWhenHandleChangesToExistingValue() {
        when(userRepository.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(serviceRepository.findById(existingService.getId())).thenReturn(Optional.of(existingService));
        when(serviceRepository.existsByHandle(updateDto.getHandle())).thenReturn(true);

        assertThrows(ServiceHandleExistsException.class,
                () -> serviceService.updateService(existingService.getId(), updateDto, owner.getEmail()));
    }

    @Test
    void updateService_succeedsForOwner() {
        when(userRepository.findByEmail(owner.getEmail())).thenReturn(Optional.of(owner));
        when(serviceRepository.findById(existingService.getId())).thenReturn(Optional.of(existingService));
        when(serviceRepository.existsByHandle(updateDto.getHandle())).thenReturn(false);
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Service updated = serviceService.updateService(existingService.getId(), updateDto, owner.getEmail());

        assertEquals(updateDto.getHandle(), updated.getHandle());
        assertEquals(updateDto.getService_name(), updated.getService_name());
    }
}
