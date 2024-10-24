package by.andersen.employee.service.impl;

import by.andersen.employee.domain.Employee;
import by.andersen.employee.exception.DataConflictException;
import by.andersen.employee.exception.ErrorMessage;
import by.andersen.employee.exception.NotFoundException;
import by.andersen.employee.repository.EmployeeRepository;
import by.andersen.employee.service.ValidationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final EmployeeRepository employeeRepository;

    @Override
    public boolean isEmailExist(String email) {
        log.info("Checking if email exist: {}", email);
        return employeeRepository.findByEmail(email).isPresent();
    }

    @Override
    public void validateManagerHasSubordinatesWithIds(List<Employee> foundedEmployees, List<Long> employeeIds) {
        log.info("Checking if manager has subordinates with ids: {}", employeeIds);

        List<Long> foundIds = foundedEmployees.stream().map(Employee::getId).toList();
        List<Long> list = employeeIds.stream().filter(id -> !foundIds.contains(id)).toList();
        if (!list.isEmpty()) {
            throw new NotFoundException("Manager hasn't got subordinates with ids " + list);
        }
    }

    @Override
    public void validateSubordinates(Long managerId, List<Long> employeeIds) {
        log.info("Validate subordinates with ids: {} for manager with id: {}", employeeIds, managerId);

        if(employeeIds.contains(managerId)){
            throw new DataConflictException(ErrorMessage.SELF_SUBORDINATION_NOT_ALLOWED);
        }
    }
}

