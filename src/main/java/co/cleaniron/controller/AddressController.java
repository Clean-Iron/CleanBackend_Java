package co.cleaniron.controller;

import co.cleaniron.model.dto.ScheduleDetailGroupedDto;
import co.cleaniron.service.AddressService;
import co.cleaniron.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("cities")
    public ResponseEntity<List<String>> getCities(){
        return ResponseEntity.ok(addressService.getCities());
    }
}
