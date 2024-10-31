package by.andersen.employee.controller;

import by.andersen.employee.dto.employee.EmployeeDetailedDto;
import by.andersen.employee.dto.employee.EmployeeDto;
import by.andersen.employee.dto.employee.EmployeeFilterDto;
import by.andersen.employee.enums.EmployeeType;
import by.andersen.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{email}")
    @ResponseStatus(OK)
    public EmployeeDetailedDto getEmployee(@PathVariable String email) {
        return employeeService.get(email);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        employeeService.delete(id);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<EmployeeDto> getAll(Pageable pageable) {
        return employeeService.getAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    public Page<EmployeeDetailedDto> search(EmployeeFilterDto employeeFilterDto, Pageable pageable) {
        return employeeService.search(employeeFilterDto, pageable);
    }

    @PutMapping("/{id}/change-type")
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(OK)
    public EmployeeDetailedDto changeType(@PathVariable Long id, @RequestParam EmployeeType type) {
        return employeeService.changeEmployeeType(id, type);
    }

}
