package by.andersen.employee.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ManagerDetailedDto extends EmployeeDetailedDto {
    private List<EmployeeDto> subordinates;
}
