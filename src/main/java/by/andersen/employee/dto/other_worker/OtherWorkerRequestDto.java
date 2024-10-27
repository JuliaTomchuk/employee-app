package by.andersen.employee.dto.other_worker;

import by.andersen.employee.dto.employee.EmployeeRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class OtherWorkerRequestDto extends EmployeeRequestDto {

    @NotBlank(message = "'description' must not be blank")
    private String description;
}
