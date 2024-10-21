package by.andersen.employee.service.impl;

import by.andersen.employee.domain.Employee;
import by.andersen.employee.domain.Worker;
import by.andersen.employee.dto.WorkerDetailedDto;
import by.andersen.employee.dto.WorkerDto;
import by.andersen.employee.dto.WorkerEditDto;
import by.andersen.employee.dto.WorkerFilterDto;
import by.andersen.employee.dto.WorkerSaveDto;
import by.andersen.employee.exception.DataConflictException;
import by.andersen.employee.exception.NotFoundException;
import by.andersen.employee.mapper.WorkerMapper;
import by.andersen.employee.repository.EmployeeRepository;
import by.andersen.employee.repository.WorkerRepository;
import by.andersen.employee.service.WorkerService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static by.andersen.employee.exception.ErrorMessage.EMAIL_ALREADY_EXISTS;
import static by.andersen.employee.exception.ErrorMessage.WORKER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerMapper workerMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    public WorkerDetailedDto save(WorkerSaveDto workerSaveDto) {
        log.info("Save worker:{}", workerSaveDto);
        if (isEmailExist(workerSaveDto.getEmail())) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }
        Worker saved = workerRepository.save(workerMapper.toEntity(workerSaveDto));

        return workerMapper.toDetailedDto(saved);
    }

    @Override
    public WorkerDetailedDto get(String email) {
        log.info("Get worker with email: {}", email);
        Worker worker = workerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(WORKER_NOT_FOUND));

        return workerMapper.toDetailedDto(worker);
    }

    @Override
    public WorkerDetailedDto update(WorkerEditDto workerEditDto) {
        log.info("Update worker with: {}", workerEditDto);

        Worker worker = workerRepository.findById(workerEditDto.getId())
                .orElseThrow(() -> new NotFoundException(WORKER_NOT_FOUND));
        String oldEmail = worker.getEmail();
        String newEmail = workerEditDto.getEmail();

        if (!StringUtils.equals(oldEmail, newEmail) && isEmailExist(workerEditDto.getEmail())) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }

        Worker updated = workerMapper.update(workerEditDto, worker);

        return workerMapper.toDetailedDto(workerRepository.save(updated));
    }

    @Override
    public Page<WorkerDto> getAll(Pageable pageable) {
        log.info(" Get all workers with pageable: {}", pageable);

        return workerRepository.findAll(pageable)
                .map(workerMapper::toDto);
    }

    @Override
    public Page<WorkerDetailedDto> search(WorkerFilterDto workerFilterDto, Pageable pageable) {
        log.info("Search workers with filter: {}", workerFilterDto);

        return workerRepository.findAll(createSpecification(workerFilterDto), pageable)
                .map(workerMapper::toDetailedDto);
    }

    private Specification<Worker> createSpecification(WorkerFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            addStringPredicate(predicates, criteriaBuilder, root, Employee.Fields.firstName, filter.getFirstName());
            addStringPredicate(predicates, criteriaBuilder, root, Employee.Fields.patronymic, filter.getPatronymic());
            addStringPredicate(predicates, criteriaBuilder, root, Employee.Fields.lastName, filter.getLastName());
            addStringPredicate(predicates, criteriaBuilder, root, Employee.Fields.email, filter.getEmail());
            addManagerEmailPredicate(predicates, criteriaBuilder, root, filter.getManagerEmail());

            addDatePredicate(predicates, criteriaBuilder, root.get(Employee.Fields.birthDate), filter.getBirthDateFrom(), filter.getBirthDateTo());
            addDatePredicate(predicates, criteriaBuilder, root.get(Employee.Fields.hireDate), filter.getHireDateFrom(), filter.getHireDateTo());

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addStringPredicate(List<Predicate> predicates, CriteriaBuilder cb, Root<Worker> root, String field,
                                    String value) {
        if (StringUtils.isNotBlank(value)) {
            predicates.add(cb.like(cb.lower(root.get(field)), value.toLowerCase() + "%"));
        }
    }

    private void addManagerEmailPredicate(List<Predicate> predicates, CriteriaBuilder cb, Root<Worker> root,
                                          String managerEmail) {
        if (StringUtils.isNotBlank(managerEmail)) {
            predicates.add(cb.like(cb.lower(root.get(Employee.Fields.manager).get(Employee.Fields.email)),
                    managerEmail.toLowerCase() + "%"));
        }
    }

    private void addDatePredicate(List<Predicate> predicates, CriteriaBuilder cb, Path<Instant> path, Instant from,
                                  Instant to) {
        if (from != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, from));
        }
        if (to != null) {
            predicates.add(cb.lessThanOrEqualTo(path, to));
        }
    }

    private boolean isEmailExist(String email) {
        return employeeRepository.findByEmail(email).isPresent();
    }

}
