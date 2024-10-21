package by.andersen.employee.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerFilterDto {
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
