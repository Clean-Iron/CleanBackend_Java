package co.cleaniron.controller;

import co.cleaniron.model.Employee;
import co.cleaniron.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Buscar empleados disponibles por fecha, horario y ciudad
     */
    @GetMapping("/available")
    public List<Employee> getAvailableEmployees(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startHour,
            @RequestParam LocalTime endHour,
            @RequestParam String city) {

        return employeeService.getAvailabilityEmployees(date,startHour,endHour,city);
    }

    @GetMapping("/id/{id}")
    public Employee getEmployeeById(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @GetMapping("/city/{city}")
    public List<Employee> getEmployeeByCity(@PathVariable String city) {
        return employeeService.getEmployeeByCity(city);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @PostMapping("/new")
    public void createNewEmployee(@RequestBody Employee employee){
        employeeService.createNewEmployee(employee);
    }

    @PatchMapping("/update/{id}")
    public Employee updateEmployeePartially(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        return employeeService.updateEmployeePartially(id, updates);
    }

    @DeleteMapping("/delete/{id}")
    public void desactivateEmployee(@PathVariable String id){
        employeeService.deactivateEmployee(id);
    }

}
