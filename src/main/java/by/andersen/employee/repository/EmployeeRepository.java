package by.andersen.employee.repository;

import by.andersen.employee.domain.Employee;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    boolean existsByEmail(String email);

    Optional<Employee> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE employees SET manager_id = null WHERE manager_id = :id", nativeQuery = true)
    void deleteManagerFromEmployees(Long id);

}
