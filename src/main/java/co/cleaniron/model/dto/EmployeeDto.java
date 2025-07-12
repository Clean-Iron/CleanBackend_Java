package co.cleaniron.model.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Objects;

@Setter
@Getter
public class EmployeeDto {
    private String employeeDocument;
    private String employeeName;
    private String employeeSurname;
    private String employeeCompleteName;

    public EmployeeDto(String employeeDocument, String employeeName, String employeeSurname) {
        this.employeeDocument = employeeDocument;
        this.employeeName = employeeName;
        this.employeeSurname = employeeSurname;
        this.employeeCompleteName = (employeeName != null ? employeeName : "") + " " +
                (employeeSurname != null ? employeeSurname : "").trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeDto that)) return false;
        return Objects.equals(employeeName, that.employeeName) &&
                Objects.equals(employeeSurname, that.employeeSurname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeName, employeeSurname);
    }
}
