package by.andersen.employee.dto.other_worker;

import by.andersen.employee.dto.employee.EmployeeDetailedDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
public class OtherWorkerDetailedDto extends EmployeeDetailedDto {
    private String description;
}
