package by.andersen.employee.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class EmployeeDetailedDto {
    private Long id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private Instant birthDate;
    private Instant hireDate;
    private String email;
    private ManagerDto manager;
}
