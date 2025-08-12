package co.cleaniron.service;

import co.cleaniron.model.City;
import co.cleaniron.repository.AddressRepository;
import co.cleaniron.repository.CityRepository;
import co.cleaniron.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final CityRepository cityRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository, CityRepository cityRepository) {
        this.addressRepository = addressRepository;
        this.cityRepository = cityRepository;
    }

    public List<String> getCities() {
        return cityRepository.findDistinctNames();
    }
}
