package co.cleaniron.model.dto;

public record EmployeeDto(
        String employeeDocument,
        String employeeName,
        String employeeSurname,
        String employeeCompleteName
) {

    public EmployeeDto(String employeeDocument, String employeeName, String employeeSurname) {
        this(
                employeeDocument,
                employeeName,
                employeeSurname,
                ((employeeName != null ? employeeName : "") + " " +
                        (employeeSurname != null ? employeeSurname : "")).trim()
        );
    }

}
