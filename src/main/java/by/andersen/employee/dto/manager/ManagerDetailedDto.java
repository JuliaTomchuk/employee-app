package by.andersen.employee.dto.manager;

import by.andersen.employee.dto.employee.EmployeeDetailedDto;
import by.andersen.employee.dto.employee.EmployeeDto;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ManagerDetailedDto extends EmployeeDetailedDto {
    private List<EmployeeDto> subordinates;
}
