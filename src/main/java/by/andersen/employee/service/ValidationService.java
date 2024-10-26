package by.andersen.employee.service;

import by.andersen.employee.domain.Employee;
import java.util.List;

public interface ValidationService {

    boolean isEmailExist(String email);

    void validateManagerHasSubordinatesWithIds(List<Employee> foundedEmployees, List<Long> employeeIds);

    void validateSubordinates(Long managerId, List<Long> employeeIds);
}
