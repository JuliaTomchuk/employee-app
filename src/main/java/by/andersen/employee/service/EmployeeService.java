package by.andersen.employee.service;

import by.andersen.employee.dto.employee.EmployeeDetailedDto;
import by.andersen.employee.dto.employee.EmployeeDto;
import by.andersen.employee.dto.employee.EmployeeFilterDto;
import by.andersen.employee.enums.EmployeeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeDetailedDto get(String email);

    Page<EmployeeDto> getAll(Pageable pageable);

    Page<EmployeeDetailedDto> search(EmployeeFilterDto employeeFilterDto, Pageable pageable);

    void delete(Long id);

    EmployeeDetailedDto changeEmployeeType(Long id, EmployeeType employeeType);

}
