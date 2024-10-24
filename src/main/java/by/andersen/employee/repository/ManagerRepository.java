package by.andersen.employee.repository;

import by.andersen.employee.domain.Manager;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ManagerRepository extends JpaRepository<Manager, Long>, JpaSpecificationExecutor<Manager> {


    @EntityGraph(attributePaths = "subordinates")
    Optional<Manager> findByEmail(String email);

    @EntityGraph(attributePaths = {"subordinates"})
    Optional<Manager> findEagerById(Long id);

}
