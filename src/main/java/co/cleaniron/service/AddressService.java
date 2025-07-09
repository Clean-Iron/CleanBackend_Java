package co.cleaniron.service;

import co.cleaniron.repository.AddressRepository;
import co.cleaniron.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

        private final AddressRepository addressRepository;

        @Autowired
        public AddressService(AddressRepository addressRepository){
            this.addressRepository = addressRepository;
        }

        public List<String> getCities(){
            return addressRepository.findListCities();
        }
}
