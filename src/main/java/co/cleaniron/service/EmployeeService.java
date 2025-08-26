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

    public Employee getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empleado no encontrado con ID: " + id));
    }

    public List<Employee> getEmployeeByCity(String city) {
        return employeeRepository.findEmployeesByCity(city);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getAvailabilityEmployees(LocalDate date, LocalTime startHour, LocalTime endHour, String city) {
        return employeeRepository.findAvailableEmployeesByDateStartHourEndHourCity(date, startHour, endHour, city);
    }

    public void createNewEmployee(Employee employee) {
        employeeRepository.save(employee);
    }

    @Transactional
    public boolean updateEmployee(String document, Employee updated) {
        Employee e = employeeRepository.findById(document).orElse(null);
        if (e == null) return false;

        if (updated.getName() != null) e.setName(updated.getName());
        if (updated.getSurname() != null) e.setSurname(updated.getSurname());
        if (updated.getPhone() != null) e.setPhone(updated.getPhone());
        if (updated.getEmail() != null) e.setEmail(updated.getEmail());
        if (updated.getAddressResidence() != null) e.setAddressResidence(updated.getAddressResidence());
        if (updated.getCity() != null) e.setCity(updated.getCity());
        if (updated.getEntryDate() != null) e.setEntryDate(updated.getEntryDate());
        if (updated.getPosition() != null) e.setPosition(updated.getPosition());
        e.setState(updated.isState());
        if (updated.getComments() != null) e.setComments(updated.getComments());

        return true;
    }

    @Transactional
    public void deactivateEmployee(String documentNumber) {
        Optional<Employee> optEmployee = employeeRepository.findById(documentNumber);
        Employee employee = optEmployee.orElse(null);
        if (employee != null) {
            employee.setState(false);
            employeeRepository.save(employee);
        }
    }
}
