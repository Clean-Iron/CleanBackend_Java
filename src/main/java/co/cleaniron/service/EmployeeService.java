package co.cleaniron.service;

import co.cleaniron.model.Employee;
import co.cleaniron.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAvailabilityEmployees(LocalDate date, LocalTime startHour, LocalTime endHour, String city){
        return employeeRepository.findAvailableEmployeesByDateStartHourEndHourCity(date, startHour, endHour, city);
    }

    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado con ID: " + id));
    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public void createNewEmployee(Employee employee){
        employeeRepository.save(employee);
    }

    public Employee updateEmployeePartially(String id, Map<String, Object> updates) {
        Employee employee = employeeRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado con ID: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> employee.setName((String) value);
                case "surname" -> employee.setSurname((String) value);
                case "email" -> employee.setEmail((String) value);
                case "phone" -> employee.setPhone((String) value);
                case "addressResidence" -> employee.setAddressResidence((String) value);
                case "city" -> employee.setCity((String) value);
                case "position" -> employee.setPosition((String) value);
                case "state" -> employee.setState((Boolean) value);
                case "fechaIngreso", "entryDate" -> {
                    if (value != null) {
                        employee.setEntryDate(LocalDate.parse((String) value));
                    }
                }
                // Agrega más campos si necesitas
            }
        });

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deactivateEmployee(String documentNumber) {
        Optional<Employee> optEmployee = employeeRepository.findById(Integer.parseInt(documentNumber));
        Employee employee = optEmployee.orElse(null);
        if (employee != null) {
            employee.setState(false); // Mark as inactive
            employeeRepository.save(employee);
        }
    }
}
