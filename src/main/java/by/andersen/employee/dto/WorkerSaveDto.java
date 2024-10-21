package by.andersen.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;

@Data
public class WorkerSaveDto {

    @NotNull(message = "'firsName' must not be blank")
    private String firstName;

    @NotBlank(message = "'patronymic' must not be blank")
    private String patronymic;

    @NotBlank(message = "'lastName' must not be blank")
    private String lastName;

    @NotNull(message = "'birthDate' must not be null")
    private Instant birthDate;

    @NotNull(message = "'hireDate' must not be null")
    private Instant hireDate;

    @NotBlank(message = "'email' must not be blank")
    @Email(message = "'email' is not a valid email address")
    private String email;
}
