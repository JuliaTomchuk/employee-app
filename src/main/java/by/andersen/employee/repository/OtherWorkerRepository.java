package by.andersen.employee.repository;

import by.andersen.employee.domain.OtherWorker;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OtherWorkerRepository extends JpaRepository<OtherWorker, Long>, JpaSpecificationExecutor<OtherWorker> {

    Optional<OtherWorker> findByEmail(String email);
}
