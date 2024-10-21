package by.andersen.employee.repository;

import by.andersen.employee.domain.Worker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkerRepository extends JpaRepository<Worker, Long>, JpaSpecificationExecutor<Worker> {

    Optional<Worker> findByEmail(String email);
}
