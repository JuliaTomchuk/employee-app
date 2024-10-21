package by.andersen.employee.dto;

import lombok.Data;

@Data
public class EmployeeDto {
    private Long id;
    private String firstName;
    private String patronymic;
    private String lastName;
    private String email;
}
