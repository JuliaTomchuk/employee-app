package by.andersen.employee.repository;

import by.andersen.employee.domain.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

   @Query(value = "Select id from employees where employees.email =:email", nativeQuery = true)
    Optional<Long> findByEmail(String email);
}
