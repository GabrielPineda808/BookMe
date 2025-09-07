package com.example.book.controller;

import com.example.book.dto.ServiceDto;
import com.example.book.model.Service;
import com.example.book.service.ServiceService;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/service")
@PreAuthorize("hasRole('USER')")
@CrossOrigin
public class ServiceController {
    private final ServiceService service;

    public ServiceController(ServiceService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createService(@RequestBody ServiceDto input, @AuthenticationPrincipal(expression = "username") String email){
        Service service1 = service.createService(input, email);
        return ResponseEntity.ok(service1);
    }
}
