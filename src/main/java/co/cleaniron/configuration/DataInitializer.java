package co.cleaniron.configuration;

import co.cleaniron.model.*;
import co.cleaniron.model.ServiceState;
import co.cleaniron.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final AddressRepository addressRepository;
    private final ServiceRepository serviceRepository;
    private final ScheduleRepository scheduleRepository;
    private final CityRepository cityRepository;

    @PostConstruct
    @Transactional
    public void initializeData() {

        // Si ya hay agendas, no volver a sembrar nada más
        if (scheduleRepository.count() > 0) return;

        upsertCities(List.of("Bogotá", "Mosquera", "Funza", "Chía", "Cajicá", "Cota", "Madrid"));

        Map<String, Employee> empleados = initEmployees();
        Map<String, Service> servicios = initServices();

        // 1) Crear clientes con direcciones (por cascada)
        Map<String, Client> clientes = initClientsWithServiceAddresses();

        // 2) Forzar escritura y volver a LEER direcciones ya gestionadas con ID
        clientRepository.flush();

        Map<String, Address> addr = new HashMap<>();
        for (Client c : clientes.values()) {
            // Traer el cliente con fetch de direcciones para asegurar entidades gestionadas
            Client managed = clientRepository.findByDocumentWithAddresses(c.getDocument())
                    .orElseThrow();
            Address first = managed.getAddresses().stream()
                    .findFirst().orElseThrow();
            addr.put(c.getDocument(), first); // Address con ID y managed
        }

        // 3) Crear agendas usando las Address gestionadas
        LocalDate lunes = LocalDate.of(2025, 8, 18);
        LocalDate martes = LocalDate.of(2025, 8, 19);
        LocalDate miercoles = LocalDate.of(2025, 8, 20);
        LocalDate jueves = LocalDate.of(2025, 8, 21);

        Schedule s1 = new Schedule();
        s1.setClient(clientes.get("900527406"));
        s1.setServiceAddress(addr.get("900527406"));
        s1.setEmployees(Set.of(empleados.get("E1"), empleados.get("E2")));
        s1.setServices(Set.of(servicios.get("ASEO"), servicios.get("VIDRIOS")));
        s1.setDate(lunes);
        s1.setStartHour(LocalTime.of(8, 0));
        s1.setEndHour(LocalTime.of(12, 0));
        s1.setState(ServiceState.PROGRAMADA);
        s1.setRecurrenceType(RecurrenceType.FRECUENTE);

        Schedule s2 = new Schedule();
        s2.setClient(clientes.get("860532081"));
        s2.setServiceAddress(addr.get("860532081"));
        s2.setEmployees(Set.of(empleados.get("E3")));
        s2.setServices(Set.of(servicios.get("OFICINA")));
        s2.setDate(miercoles);
        s2.setStartHour(LocalTime.of(7, 30));
        s2.setEndHour(LocalTime.of(10, 30));
        s2.setState(ServiceState.PROGRAMADA);
        s2.setRecurrenceType(RecurrenceType.FRECUENTE);

        Schedule s3 = new Schedule();
        s3.setClient(clientes.get("901199585"));
        s3.setServiceAddress(addr.get("901199585"));
        s3.setEmployees(Set.of(empleados.get("E4"), empleados.get("E5")));
        s3.setServices(Set.of(servicios.get("OFICINA"), servicios.get("VIDRIOS")));
        s3.setDate(jueves);
        s3.setStartHour(LocalTime.of(8, 0));
        s3.setEndHour(LocalTime.of(12, 0));
        s3.setState(ServiceState.PROGRAMADA);
        s3.setRecurrenceType(RecurrenceType.FRECUENTE);

        Schedule s4 = new Schedule();
        s4.setClient(clientes.get("900603565"));
        s4.setServiceAddress(addr.get("900603565"));
        s4.setEmployees(Set.of(empleados.get("E2")));
        s4.setServices(Set.of(servicios.get("OFICINA")));
        s4.setDate(jueves);
        s4.setStartHour(LocalTime.of(13, 0));
        s4.setEndHour(LocalTime.of(16, 0));
        s4.setState(ServiceState.PROGRAMADA);
        s4.setRecurrenceType(RecurrenceType.FRECUENTE);

        Schedule s5 = new Schedule();
        s5.setClient(clientes.get("832007178"));
        s5.setServiceAddress(addr.get("832007178"));
        s5.setEmployees(Set.of(empleados.get("E1")));
        s5.setServices(Set.of(servicios.get("ASEO")));
        s5.setDate(martes);
        s5.setStartHour(LocalTime.of(7, 0));
        s5.setEndHour(LocalTime.of(11, 0));
        s5.setState(ServiceState.PROGRAMADA);
        s5.setRecurrenceType(RecurrenceType.FRECUENTE);

        scheduleRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5));
    }

    private void upsertCities(List<String> names) {
        // Insertar solo las que no existan
        for (String name : names) {
            boolean exists = cityRepository.findAll().stream()
                    .anyMatch(c -> c.getName().equalsIgnoreCase(name));
            if (!exists) {
                cityRepository.save(new City(null, name));
            }
        }
    }

    private Map<String, Employee> initEmployees() {
        Employee e1 = new Employee();
        e1.setDocument("1018403098");
        e1.setName("Dylan Santiago Calvo");
        e1.setSurname("Pulido");
        e1.setPhone("3206830239");
        e1.setCity("Bogotá");
        e1.setPosition("Operario");
        e1.setState(true);

        Employee e2 = new Employee();
        e2.setDocument("57439773");
        e2.setName("Emith Rocio Tamara");
        e2.setSurname("Agamez");
        e2.setPhone("3184757726");
        e2.setCity("Funza");
        e2.setPosition("Operario");
        e2.setState(true);

        Employee e3 = new Employee();
        e3.setDocument("85373154");
        e3.setName("Fabian Segundo Hernandez");
        e3.setSurname("Uribe");
        e3.setPhone("3043703005");
        e3.setCity("Bogotá");
        e3.setPosition("Operario");
        e3.setState(true);

        Employee e4 = new Employee();
        e4.setDocument("1149202976");
        e4.setName("Jazmin Yuleika Gomez");
        e4.setSurname("Rivas");
        e4.setPhone("3132028931");
        e4.setCity("Mosquera");
        e4.setPosition("Operario");
        e4.setState(true);

        Employee e5 = new Employee();
        e5.setDocument("52067060");
        e5.setName("Luz Dary");
        e5.setSurname("Chávez Chimbi");
        e5.setPhone("3132937942");
        e5.setCity("Bogotá");
        e5.setPosition("Operario");
        e5.setState(true);

        employeeRepository.saveAll(Arrays.asList(e1, e2, e3, e4, e5));

        Map<String, Employee> map = new LinkedHashMap<>();
        map.put("E1", e1);
        map.put("E2", e2);
        map.put("E3", e3);
        map.put("E4", e4);
        map.put("E5", e5);
        return map;
    }

    private Map<String, Service> initServices() {
        Service aseoGeneral = new Service();
        aseoGeneral.setDescription("Aseo General");
        aseoGeneral.setHours(4);
        aseoGeneral.setCost(180.0);
        Service oficinas = new Service();
        oficinas.setDescription("Aseo de Oficinas");
        oficinas.setHours(3);
        oficinas.setCost(150.0);
        Service vidrios = new Service();
        vidrios.setDescription("Limpieza de Vidrios");
        vidrios.setHours(2);
        vidrios.setCost(120.0);
        serviceRepository.saveAll(Arrays.asList(aseoGeneral, oficinas, vidrios));
        Map<String, Service> map = new LinkedHashMap<>();
        map.put("ASEO", aseoGeneral);
        map.put("OFICINA", oficinas);
        map.put("VIDRIOS", vidrios);
        return map;
    }

    private Map<String, Client> initClientsWithServiceAddresses() {
        Client c1 = new Client("900527406", "NIT", "Padilla", "& Compañia", "3187219976", null);
        c1.getAddresses().add(new Address("Calle 6 Sur #15A-39, Cond. Fortaleza San Telmo, Casa 13, Vereda Canelón", "Cajicá", "Sitio de servicio") {{
            setClient(c1);
        }});

        Client c2 = new Client("860532081", "NIT", "Halcon", "Agroindustrial SAS", "3219268372", null);
        c2.getAddresses().add(new Address("KM 1.5 Vía Chía - Cajicá, Ed. OXUS Of. 512", "Chía", "Sitio de servicio") {{
            setClient(c2);
        }});

        Client c3 = new Client("901199585", "NIT", "DVA", "", "3185864395", null);
        c3.getAddresses().add(new Address("KM 1.5 Vía Chía - Cajicá, Ed. OXUS Of. 514", "Chía", "Sitio de servicio") {{
            setClient(c3);
        }});

        Client c4 = new Client("900603565", "NIT", "Dissertum", "Financial", "3102211872", null);
        c4.getAddresses().add(new Address("Km 1.5 Vía Cajicá - Chía, Centro Empresarial OXUS Of. 207", "Chía", "Sitio de servicio") {{
            setClient(c4);
        }});

        Client c5 = new Client("832007178", "NIT", "Multiglobal", "", "3138161441", null);
        c5.getAddresses().add(new Address("Parque Industrial Santo Domingo, Lote 9 Manzana H, Variante de Madrid", "Mosquera", "Sitio de servicio") {{
            setClient(c5);
        }});

        clientRepository.saveAll(Arrays.asList(c1, c2, c3, c4, c5));

        Map<String, Client> map = new LinkedHashMap<>();
        map.put(c1.getDocument(), c1);
        map.put(c2.getDocument(), c2);
        map.put(c3.getDocument(), c3);
        map.put(c4.getDocument(), c4);
        map.put(c5.getDocument(), c5);
        return map;
    }
}
