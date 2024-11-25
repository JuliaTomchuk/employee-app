package by.andersen.employee.service.impl;

import by.andersen.employee.domain.Employee;
import by.andersen.employee.domain.Manager;
import by.andersen.employee.dto.employee.EmployeeDetailedDto;
import by.andersen.employee.dto.employee.EmployeeDto;
import by.andersen.employee.dto.employee.EmployeeFilterDto;
import by.andersen.employee.enums.EmployeeType;
import by.andersen.employee.exception.DataConflictException;
import by.andersen.employee.exception.ErrorMessage;
import by.andersen.employee.exception.NotFoundException;
import by.andersen.employee.mapper.EmployeeMapper;
import by.andersen.employee.repository.EmployeeRepository;
import by.andersen.employee.service.EmployeeService;
import by.andersen.employee.service.ManagerService;
import by.andersen.employee.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final ManagerService managerService;

    @Override
    public EmployeeDetailedDto get(String email) {
        log.info("Get employee by email: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EMPLOYEE_NOT_FOUND));
        return employeeMapper.toDetailedDto(employee);
    }

    @Override
    public Page<EmployeeDto> getAll(Pageable pageable) {
        log.info("Get all employees with pageable: {}", pageable);

        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toDto);
    }

    @Override
    public Page<EmployeeDetailedDto> search(EmployeeFilterDto employeeFilterDto, Pageable pageable) {
        log.info("Search employees with filter: {} and pageable: {}", employeeFilterDto, pageable);

        return employeeRepository
                .findAll(SpecificationUtils.createSpecificationForEmployeeHierarchy(employeeFilterDto), pageable)
                .map(employeeMapper::toDetailedDto);
    }

    @Override
    public void delete(Long id) {
        log.info("Delete employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EMPLOYEE_NOT_FOUND));

        if (employee instanceof Manager) {
            managerService.deleteManagerFromAllEmployees(id);
        }

        employeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public EmployeeDetailedDto changeEmployeeType(Long id, EmployeeType employeeType) {
        log.info("Change employee type with id: {} to employee type: {}", id, employeeType);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EMPLOYEE_NOT_FOUND));

        if (employee.getClass().getSimpleName().equalsIgnoreCase(employeeType.getValue())) {
            throw new DataConflictException(ErrorMessage.EMPLOYEE_ALREADY_IN_THIS_POSITION);
        }

        delete(id);

        Employee updated = null;
        switch (employeeType) {
            case WORKER -> updated = employeeMapper.toWorker(employee);
            case MANAGER -> updated = employeeMapper.toManager(employee);
            case OTHER_WORKER -> updated = employeeMapper.toOtherWorker(employee);
        }

        return employeeMapper.toDetailedDto(employeeRepository.save(updated));
    }

}
