package com.example.book.service;

import com.example.book.dto.ServiceDto;
import com.example.book.model.Location;
import com.example.book.model.User;
import com.example.book.repository.ServiceRepository;
import com.example.book.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public ServiceService(ServiceRepository serviceRepository, UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    public com.example.book.model.Service createService(ServiceDto input, String email){
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        com.example.book.model.Service service = new com.example.book.model.Service();
        service.setUser(owner);
        service.setService_name(input.getService_name());
        service.setHandle(input.getHandle());
        service.setLocation(input.getLocation());
        service.setDesc(input.getDesc());
        service.setDesc(input.getDesc());
        service.setOpen(input.getOpen());
        service.setClose(input.getClose());
        service.setInterval(input.getInterval());
        if (input.getLocation() != null) {
            Location loc = new Location();
            loc.setAddress(input.getLocation().getAddress());
            loc.setCity(input.getLocation().getCity());
            loc.setState(input.getLocation().getState());
            loc.setArea_code(input.getLocation().getArea_code());
            loc.setCountry(input.getLocation().getCountry());
            service.setLocation(loc);
        }
        serviceRepository.save(service);

        return service;
    }
}
