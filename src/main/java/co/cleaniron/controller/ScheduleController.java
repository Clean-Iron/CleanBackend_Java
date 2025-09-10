package co.cleaniron.controller;

import co.cleaniron.model.Schedule;
import co.cleaniron.model.dto.ScheduleDetailGroupedDto;
import co.cleaniron.model.dto.ScheduleUpdateDto;
import co.cleaniron.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping
    public void createNewSchedule(@RequestBody List<Schedule> schedules) {
        scheduleService.createSchedule(schedules);
    }

    @GetMapping("/servicesEmployee/{employeeDoc}")
    public ResponseEntity<List<ScheduleDetailGroupedDto>> getServicesFromEmployeeByMonth(
            @PathVariable("employeeDoc") String employeeDoc,
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) {
        return ResponseEntity.ok(scheduleService.getServicesFromEmployeeByMonth(employeeDoc, year, month));
    }

    @GetMapping("/servicesClient/{clientDoc}")
    public ResponseEntity<List<ScheduleDetailGroupedDto>> getServicesFromClientByMonth(
            @PathVariable("clientDoc") String clientDoc,
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) {
        return ResponseEntity.ok(scheduleService.getServicesFromClientByMonth(clientDoc, year, month));
    }

    @GetMapping("/servicesCity/{city}")
    public ResponseEntity<List<ScheduleDetailGroupedDto>> getServicesFromCityByMonth(
            @PathVariable("city") String city,
            @RequestParam("year") String year,
            @RequestParam("month") String month
    ) {
        return ResponseEntity.ok(scheduleService.getServicesFromCityByMonth(city, year, month));
    }

    @GetMapping("/{dateService}")
    public ResponseEntity<List<ScheduleDetailGroupedDto>> getAllByDate(
            @PathVariable("dateService") LocalDate dateService
    ) {
        List<ScheduleDetailGroupedDto> result =
                scheduleService.getScheduleDetailsByDate(dateService);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{dateService}/filter")
    public ResponseEntity<List<ScheduleDetailGroupedDto>> getByDateWithFilters(
            @PathVariable("dateService") LocalDate dateService,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surname", required = false) String surname
    ) {
        List<ScheduleDetailGroupedDto> result =
                scheduleService.getScheduleDetailsByDateCityClient(dateService, city, name, surname);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody ScheduleUpdateDto dto) {
        try {
            Schedule updated = scheduleService.updateSchedulePartial(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
    }
}
