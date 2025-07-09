package co.cleaniron.controller;

import co.cleaniron.model.Service;
import co.cleaniron.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service")
public class ServiceController {
    private final ServiceService serviceService;

    @Autowired
    public ServiceController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @GetMapping("/all")
    public List<Service> getAllServices() {
        return serviceService.getAllServices();
    }

}
