package by.andersen.employee.dto.employee;

import java.time.Instant;
import lombok.Data;

@Data
public class EmployeeFilterDto {
    private String firstName;
    private String patronymic;
    private String lastName;
    private Instant birthDateFrom;
    private Instant birthDateTo;
    private Instant hireDateFrom;
    private Instant hireDateTo;
    private String email;
    private String managerEmail;
}
