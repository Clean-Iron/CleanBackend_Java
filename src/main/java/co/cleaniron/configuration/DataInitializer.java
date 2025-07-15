package co.cleaniron.configuration;

import co.cleaniron.model.*;
import co.cleaniron.model.ServiceState;
import co.cleaniron.repository.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @PostConstruct
    public void initializeData() {
        // Check if data already exists to avoid duplicates
        if (clientRepository.count() > 0) {
            return; // Data already initialized
        }

        // Initialize clients first
        initClients();

        // Then initialize services and schedules
        initServicesAndSchedules();
    }

    private void initClients() {
        List<Client> clients = new ArrayList<>();

        Client client1 = new Client("12345678", "NIT", "Carlos Andrés", "Castañeda Lozano",
                "612345678", "car.castaneda27@gmail.com");

        Address address1_1 = new Address("Calle Mayor 15, 3B", "Madrid", "Residencia");
        Address address1_2 = new Address("Polígono Industrial Norte, Nave 7", "Madrid", "Trabajo");

        address1_1.setClient(client1);
        address1_2.setClient(client1);

        client1.getAddresses().add(address1_1);
        client1.getAddresses().add(address1_2);

        clients.add(client1);

        Client client2 = new Client("1000318916", "CC", "Daniel Felipe", "Rojas Hernández",
                "3015608604", "dani.07rojas94@gmail.com");

        Address address2_1 = new Address("Av. Diagonal 213, 5A", "Barcelona", "Residencia");
        address2_1.setClient(client2);
        client2.getAddresses().add(address2_1);

        clients.add(client2);

        Client client3 = new Client("34567890", "CC", "Carlos", "Ruiz",
                "634567890", "carlos@diseñoscr.com");

        Address address3_1 = new Address("Plaza del Ayuntamiento 4, 2C", "Valencia", "Residencia");
        Address address3_2 = new Address("Calle Colón 76, Local 2", "Valencia", "Trabajo");
        Address address3_3 = new Address("Calle Serrano 25", "Madrid", "Secundaria");

        address3_1.setClient(client3);
        address3_2.setClient(client3);
        address3_3.setClient(client3);

        client3.getAddresses().add(address3_1);
        client3.getAddresses().add(address3_2);
        client3.getAddresses().add(address3_3);

        clients.add(client3);

        Client client4 = new Client("45678901", "CC", "Ana", "Martínez",
                "645678901", "ana.martinez@outlook.com");

        Address address4_1 = new Address("Av. de la Constitución 32, 7D", "Cali", "Residencia");
        address4_1.setClient(client4);
        client4.getAddresses().add(address4_1);

        clients.add(client4);

        Client client5 = new Client("56789012", "CC", "Pablo", "Sánchez",
                "656789012", "pablo@innovaplus.com");

        Address address5_1 = new Address("Gran Vía 45, 8B", "Bilbao", "Residencia");
        Address address5_2 = new Address("Parque Tecnológico, Edificio 3", "Cartagena", "Trabajo");

        address5_1.setClient(client5);
        address5_2.setClient(client5);

        client5.getAddresses().add(address5_1);
        client5.getAddresses().add(address5_2);

        clients.add(client5);

        clientRepository.saveAll(clients);
    }

    private void initServicesAndSchedules() {
        // Create services
        Service limpiezaOficinas = new Service();
        limpiezaOficinas.setDescription("Limpieza de Oficinas");
        limpiezaOficinas.setHours(3);
        limpiezaOficinas.setCost(150.0);
        serviceRepository.save(limpiezaOficinas);

        Service limpiezaMuebles = new Service();
        limpiezaMuebles.setDescription("Limpieza de Muebles");
        limpiezaMuebles.setHours(3);
        limpiezaMuebles.setCost(150.0);
        serviceRepository.save(limpiezaMuebles);

        Service limpiezaTapetes = new Service();
        limpiezaTapetes.setDescription("Limpieza de Tapetes");
        limpiezaTapetes.setHours(3);
        limpiezaTapetes.setCost(150.0);
        serviceRepository.save(limpiezaTapetes);

        // Create business clients (avoid duplicate document numbers)
        Client corporativoABC = new Client("88888888", "NIT", "Corporativo", "ABC", "555-0001", "contacto@corporativoabc.com");
        Client tecnologiaXYZ = new Client("77777777", "NIT", "Tecnología", "XYZ", "555-0002", "info@tecnologiaxyz.com");
        Client consultores123 = new Client("66666666", "NIT", "Consultores", "123", "555-0003", "admin@consultores123.com");
        Client segurosEficaces = new Client("55555555", "NIT", "Seguros", "Eficaces", "555-0004", "contacto@seguroseficaces.com");

        clientRepository.saveAll(Arrays.asList(corporativoABC, tecnologiaXYZ, consultores123, segurosEficaces));

        // Create addresses
        Address addr1 = new Address();
        addr1.setAddress("Av. Principal #123");
        addr1.setCity("Bogota");
        addr1.setClient(corporativoABC);

        Address addr2 = new Address();
        addr2.setAddress("Calle Comercial #45");
        addr2.setCity("Cali");
        addr2.setClient(tecnologiaXYZ);

        Address addr3 = new Address();
        addr3.setAddress("Plaza Central #78");
        addr3.setCity("Cartagena");
        addr3.setClient(consultores123);

        Address addr4 = new Address();
        addr4.setAddress("Av. Central #290");
        addr4.setCity("Sevilla");
        addr4.setClient(segurosEficaces);

        addressRepository.saveAll(Arrays.asList(addr1, addr2, addr3, addr4));

        // Update clients with their addresses
        corporativoABC.getAddresses().add(addr1);
        tecnologiaXYZ.getAddresses().add(addr2);
        consultores123.getAddresses().add(addr3);
        segurosEficaces.getAddresses().add(addr4);

        clientRepository.saveAll(Arrays.asList(corporativoABC, tecnologiaXYZ, consultores123, segurosEficaces));

        // Create employees
        Employee juan = new Employee();
        juan.setDocument("10001001");
        juan.setName("Juan");
        juan.setSurname("García");
        juan.setPhone("555-1001");
        juan.setEmail("juan.garcia@cleaniron.co");
        juan.setAddressResidence("Calle Residencial 1");
        juan.setCity("Cartagena");
        juan.setPosition("Operario");
        juan.setState(true);

        Employee maria = new Employee();
        maria.setDocument("10001002");
        maria.setName("María");
        maria.setSurname("López");
        maria.setPhone("555-1002");
        maria.setEmail("maria.lopez@cleaniron.co");
        maria.setAddressResidence("Calle Residencial 2");
        maria.setCity("Sevilla");
        maria.setPosition("Operario");
        maria.setState(false);

        Employee ana = new Employee();
        ana.setDocument("10001003");
        ana.setName("Ana");
        ana.setSurname("Martínez");
        ana.setPhone("555-1003");
        ana.setEmail("ana.martinez@cleaniron.co");
        ana.setAddressResidence("Calle Residencial 3");
        ana.setCity("Barcelona");
        ana.setPosition("Operario");
        ana.setState(true);

        Employee carlos = new Employee();
        carlos.setDocument("10001004");
        carlos.setName("Carlos");
        carlos.setSurname("Pérez");
        carlos.setPhone("555-1004");
        carlos.setEmail("carlos.perez@cleaniron.co");
        carlos.setAddressResidence("Calle Residencial 4");
        carlos.setCity("Bilbao");
        carlos.setPosition("Operario");
        carlos.setState(false);

        Employee pedro = new Employee();
        pedro.setDocument("10001005");
        pedro.setName("Pedro");
        pedro.setSurname("Sánchez");
        pedro.setPhone("555-1005");
        pedro.setEmail("pedro.sanchez@cleaniron.co");
        pedro.setAddressResidence("Calle Residencial 5");
        pedro.setCity("Madrid");
        pedro.setPosition("Operario");
        pedro.setState(true);

        Employee lucia = new Employee();
        lucia.setDocument("10001006");
        lucia.setName("Lucía");
        lucia.setSurname("Torres");
        lucia.setPhone("555-1006");
        lucia.setEmail("lucia.torres@cleaniron.co");
        lucia.setAddressResidence("Calle Residencial 6");
        lucia.setCity("Madrid");
        lucia.setPosition("Operario");
        lucia.setState(true);

        Employee roberto = new Employee();
        roberto.setDocument("10001007");
        roberto.setName("Roberto");
        roberto.setSurname("López");
        roberto.setPhone("555-1007");
        roberto.setEmail("roberto.lopez@cleaniron.co");
        roberto.setAddressResidence("Calle Residencial 7");
        roberto.setCity("Madrid");
        roberto.setPosition("Operario");
        roberto.setState(true);

        Employee diana = new Employee();
        diana.setDocument("10001008");
        diana.setName("Diana");
        diana.setSurname("Vega");
        diana.setPhone("555-1008");
        diana.setEmail("diana.vega@cleaniron.co");
        diana.setAddressResidence("Calle Residencial 8");
        diana.setCity("Cartagena");
        diana.setPosition("Operario");
        diana.setState(true);

        employeeRepository.saveAll(Arrays.asList(juan, maria, ana, carlos, pedro, lucia, roberto, diana));

        // Create schedules
        Schedule schedule1 = new Schedule();
        schedule1.setClient(corporativoABC);
        schedule1.setServiceAddress(addr1);
        schedule1.setEmployees(Set.of(juan, maria));
        schedule1.setServices(Set.of(limpiezaOficinas));
        schedule1.setDate(LocalDate.of(2025, 4, 3));
        schedule1.setStartHour(LocalTime.of(8, 0));
        schedule1.setEndHour(LocalTime.of(10, 30));
        schedule1.setState(ServiceState.COMPLETADA);

        Schedule schedule2 = new Schedule();
        schedule2.setClient(tecnologiaXYZ);
        schedule2.setServiceAddress(addr2);
        schedule2.setEmployees(Set.of(ana, carlos));
        schedule2.setServices(Set.of(limpiezaOficinas));
        schedule2.setDate(LocalDate.of(2025, 4, 4));
        schedule2.setStartHour(LocalTime.of(11, 0));
        schedule2.setEndHour(LocalTime.of(13, 30));
        schedule2.setState(ServiceState.PROGRAMADA);

        Schedule schedule3 = new Schedule();
        schedule3.setClient(corporativoABC);
        schedule3.setServiceAddress(addr1);
        schedule3.setEmployees(Set.of(juan, maria));
        schedule3.setServices(Set.of(limpiezaOficinas));
        schedule3.setDate(LocalDate.of(2025, 4, 15));
        schedule3.setStartHour(LocalTime.of(8, 0));
        schedule3.setEndHour(LocalTime.of(13, 0));
        schedule3.setState(ServiceState.CANCELADA);

        Schedule schedule4 = new Schedule();
        schedule4.setClient(corporativoABC);
        schedule4.setServiceAddress(addr1);
        schedule4.setEmployees(Set.of(juan, maria));
        schedule4.setServices(Set.of(limpiezaOficinas));
        schedule4.setDate(LocalDate.of(2025, 4, 15));
        schedule4.setStartHour(LocalTime.of(14, 0));
        schedule4.setEndHour(LocalTime.of(19, 0));
        schedule4.setState(ServiceState.CANCELADA);

        Schedule schedule5 = new Schedule();
        schedule5.setClient(tecnologiaXYZ);
        schedule5.setServiceAddress(addr2);
        schedule5.setEmployees(Set.of(ana, carlos));
        schedule5.setServices(Set.of(limpiezaOficinas));
        schedule5.setDate(LocalDate.of(2025, 4, 15));
        schedule5.setStartHour(LocalTime.of(11, 0));
        schedule5.setEndHour(LocalTime.of(13, 30));
        schedule5.setState(ServiceState.CANCELADA);

        Schedule schedule6 = new Schedule();
        schedule6.setClient(consultores123);
        schedule6.setServiceAddress(addr3);
        schedule6.setEmployees(Set.of(pedro, lucia));
        schedule6.setServices(Set.of(limpiezaOficinas));
        schedule6.setDate(LocalDate.of(2025, 4, 15));
        schedule6.setStartHour(LocalTime.of(14, 30));
        schedule6.setEndHour(LocalTime.of(16, 0));
        schedule6.setState(ServiceState.PROGRAMADA);

        Schedule schedule7 = new Schedule();
        schedule7.setClient(segurosEficaces);
        schedule7.setServiceAddress(addr4);
        schedule7.setEmployees(Set.of(roberto, diana));
        schedule7.setServices(Set.of(limpiezaOficinas));
        schedule7.setDate(LocalDate.of(2025, 4, 15));
        schedule7.setStartHour(LocalTime.of(17, 0));
        schedule7.setEndHour(LocalTime.of(18, 30));
        schedule7.setState(ServiceState.PROGRAMADA);

        scheduleRepository.saveAll(Arrays.asList(schedule1, schedule2, schedule3, schedule4, schedule5, schedule6, schedule7));
    }
}

