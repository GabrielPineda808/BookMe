package com.example.book.controller;

import com.example.book.dto.ServiceDto;
import com.example.book.model.Service;
import com.example.book.response.ServiceResponse;
import com.example.book.service.ServiceManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
@PreAuthorize("hasRole('ROLE_USER')")
@CrossOrigin
@AllArgsConstructor
@Tag(name = "Services", description = "Service listing and management")
@SecurityRequirement(name = "bearerAuth")
public class ServiceController {
    private final ServiceManagementService serviceService;

    @GetMapping
    @Operation(summary = "List services", description = "Get all enabled services")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Services returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> getAllServices(){
        List<Service> services = serviceService.getAllServices();
        return ResponseEntity.ok(services.stream().map(ServiceResponse::fromService).toList());
    }

    @PostMapping("/create-service")
    @Operation(summary = "Create service", description = "Create a new service listing")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service created"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> createService(@Valid @RequestBody ServiceDto input, @AuthenticationPrincipal(expression = "username") String email){
        Service service = serviceService.createService(input, email);
        return ResponseEntity.ok(ServiceResponse.fromService(service));
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Update service", description = "Update an existing service listing")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service updated"),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> updateService(@PathVariable Long id, @Valid @RequestBody ServiceDto input, @AuthenticationPrincipal(expression = "username") String email){
        Service updatedService = serviceService.updateService(id, input, email);
        return ResponseEntity.ok(ServiceResponse.fromService(updatedService));
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Delete service", description = "Delete a service listing")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service deleted"),
            @ApiResponse(responseCode = "400", description = "Deletion blocked", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> deleteService(@PathVariable Long id, @RequestParam(defaultValue = "false") boolean forceDelete, @AuthenticationPrincipal(expression = "username") String email){
        serviceService.deleteService(id, email, forceDelete);
        return ResponseEntity.ok("Service deleted successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get service", description = "Fetch a service by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service returned"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<?> getService(@PathVariable Long id){
        Service service = serviceService.getServiceById(id);
        return ResponseEntity.ok(ServiceResponse.fromService(service));
    }

    @GetMapping("/handle/{handle}")
    @Operation(summary = "Get service by handle", description = "Fetch a service by handle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service returned"),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public ResponseEntity<?> getServiceByHandle(@PathVariable String handle){
        Service service = serviceService.getServiceByHandle(handle);
        return ResponseEntity.ok(ServiceResponse.fromService(service));
    }

    @GetMapping("/my-services")
    @Operation(summary = "My services", description = "List services owned by the current user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Services returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> getMyServices(@AuthenticationPrincipal(expression = "username") String email){
        List<Service> services = serviceService.getServicesByOwner(email);
        return ResponseEntity.ok(services.stream().map(ServiceResponse::fromService).toList());
    }

    @GetMapping("/search/city/{city}")
    @Operation(summary = "Search by city", description = "Find services in a city")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Services returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> getServicesByCity(@PathVariable String city){
        List<Service> services = serviceService.getServicesByCity(city);
        return ResponseEntity.ok(services.stream().map(ServiceResponse::fromService).toList());
    }

    @GetMapping("/search/state/{state}")
    @Operation(summary = "Search by state", description = "Find services in a state")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Services returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> getServicesByState(@PathVariable String state){
        List<Service> services = serviceService.getServicesByState(state);
        return ResponseEntity.ok(services.stream().map(ServiceResponse::fromService).toList());
    }

    @PostMapping("/{id}/analyze-impact")
    @Operation(summary = "Analyze impact", description = "Check existing bookings impacted by proposed changes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Analysis returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    public ResponseEntity<?> analyzeServiceUpdateImpact(@PathVariable Long id, @RequestBody ServiceDto proposedChanges){
        ServiceManagementService.ServiceImpactAnalysis impact = serviceService.analyzeServiceUpdateImpact(id, proposedChanges);
        return ResponseEntity.ok(impact);
    }

}
