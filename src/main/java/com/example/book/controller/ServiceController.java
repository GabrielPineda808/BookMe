package com.example.book.controller;

import com.example.book.dto.ServiceDto;
import com.example.book.model.Service;
import com.example.book.response.ServiceResponse;
import com.example.book.service.ServiceService;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/service")
@PreAuthorize("hasAuthority('ROLE_USER')")
@CrossOrigin
public class ServiceController {
    private final ServiceService service;

    public ServiceController(ServiceService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createService(@RequestBody ServiceDto input, @AuthenticationPrincipal(expression = "username") String email){
        Service service1 = service.createService(input, email);
        ServiceResponse response = new ServiceResponse();
        response.setClose(service1.getClose());
        response.setService_name(service1.getService_name());
        response.setClose(service1.getClose());
        response.setDesc(service1.getDesc());
        response.setHandle(service1.getHandle());
        response.setLocation(service1.getLocation());
        response.setInterval(service1.getInterval());

        return ResponseEntity.ok(service1);
    }
}
