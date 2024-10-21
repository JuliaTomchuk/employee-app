package by.andersen.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerEditDto {

    @NotNull(message = "'id' must not be null")
    private Long id;

    @NotBlank(message = "'firstName' must not be blank")
    private String firstName;

    @NotBlank(message = "'patronymic' must not be blank")
    private String patronymic;

    @NotBlank(message = "'lastName' must not be blank")
    private String lastName;

    @NotNull(message = "'birthDate' must not be null")
    private Instant birthDate;

    @NotNull(message = "'hireDate' must not be null")
    private Instant hireDate;

    @Email(message = "'email' is not a valid email address")
    @NotBlank(message = "'email' must not be blank")
    private String email;
}
