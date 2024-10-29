package by.andersen.employee.mapper;

import by.andersen.employee.domain.Employee;
import by.andersen.employee.domain.Manager;
import by.andersen.employee.domain.OtherWorker;
import by.andersen.employee.domain.Worker;
import by.andersen.employee.dto.employee.EmployeeDetailedDto;
import by.andersen.employee.dto.employee.EmployeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDetailedDto toDetailedDto(Employee employee);

    EmployeeDto toDto(Employee employee);

    Worker toWorker(Employee employee);

    @Mapping(target = "subordinates", ignore = true)
    Manager toManager(Employee employee);

    @Mapping(target = "description", ignore = true)
    OtherWorker toOtherWorker(Employee employee);
}
