package com.example.book.controller;

import com.example.book.dto.ServiceDto;
import com.example.book.model.Service;
import com.example.book.response.ServiceResponse;
import com.example.book.service.ServiceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
@PreAuthorize("hasAuthority('ROLE_USER')")
@CrossOrigin
public class ServiceController {
    private final ServiceService serviceService;

    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping("/create-service")
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceDto input, @AuthenticationPrincipal(expression = "username") String email){
        Service service = serviceService.createService(input, email);
        return ResponseEntity.ok(ServiceResponse.fromService(service));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDto input, @AuthenticationPrincipal(expression = "username") String email){
        Service updatedService = serviceService.updateService(id, input, email);
        return ResponseEntity.ok(ServiceResponse.fromService(updatedService));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteService(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean forceDelete, @AuthenticationPrincipal(expression = "username") String email){
        serviceService.deleteService(id, email, forceDelete);
        return ResponseEntity.ok("Service deleted successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getService(@PathVariable Long id){
        Service service = serviceService.getServiceById(id);
        return ResponseEntity.ok(ServiceResponse.fromService(service));
    }

    @GetMapping("/handle/{handle}")
    public ResponseEntity<?> getServiceByHandle(@PathVariable String handle){
        Service service = serviceService.getServiceByHandle(handle);
        return ResponseEntity.ok(ServiceResponse.fromService(service));
    }

    @GetMapping("/my-services")
    public ResponseEntity<?> getMyServices(@AuthenticationPrincipal(expression = "username") String email){
        List<Service> services = serviceService.getServicesByOwner(email);
        return ResponseEntity.ok(services.stream().map(ServiceResponse::fromService).toList());
    }

    @GetMapping("/search/city/{city}")
    public ResponseEntity<?> getServicesByCity(@PathVariable String city){
        List<Service> services = serviceService.getServicesByCity(city);
        return ResponseEntity.ok(services.stream().map(ServiceResponse::fromService).toList());
    }

    @GetMapping("/search/state/{state}")
    public ResponseEntity<?> getServicesByState(@PathVariable String state){
        List<Service> services = serviceService.getServicesByState(state);
        return ResponseEntity.ok(services.stream().map(ServiceResponse::fromService).toList());
    }

    @PostMapping("/{id}/analyze-impact")
    public ResponseEntity<?> analyzeServiceUpdateImpact(@PathVariable Long id, @RequestBody ServiceDto proposedChanges){
        ServiceService.ServiceImpactAnalysis impact = serviceService.analyzeServiceUpdateImpact(id, proposedChanges);
        return ResponseEntity.ok(impact);
    }

}
