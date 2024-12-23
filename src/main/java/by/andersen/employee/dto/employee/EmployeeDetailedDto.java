package by.andersen.employee.dto.employee;

import by.andersen.employee.dto.manager.ManagerDto;
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
    private String createdBy;
    private String modifiedBy;
    private Instant createdDate;
    private Instant modifiedDate;
}
