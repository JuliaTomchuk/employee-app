package by.andersen.employee.service.impl;

import by.andersen.employee.domain.Worker;
import by.andersen.employee.dto.worker.WorkerDetailedDto;
import by.andersen.employee.dto.worker.WorkerDto;
import by.andersen.employee.dto.worker.WorkerFilterDto;
import by.andersen.employee.dto.worker.WorkerRequestDto;
import by.andersen.employee.exception.DataConflictException;
import by.andersen.employee.exception.NotFoundException;
import by.andersen.employee.mapper.WorkerMapper;
import by.andersen.employee.repository.WorkerRepository;
import by.andersen.employee.service.ValidationService;
import by.andersen.employee.service.WorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static by.andersen.employee.exception.ErrorMessage.EMAIL_ALREADY_EXISTS;
import static by.andersen.employee.exception.ErrorMessage.WORKER_NOT_FOUND;
import static by.andersen.employee.utils.SpecificationUtils.createSpecificationForEmployeeHierarchy;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerMapper workerMapper;
    private final ValidationService validationService;

    @Override
    @Transactional
    public WorkerDetailedDto save(WorkerRequestDto workerRequestDto) {
        log.info("Save worker:{}", workerRequestDto);
        if (validationService.isEmailExist(workerRequestDto.getEmail())) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }
        Worker saved = workerRepository.save(workerMapper.toEntity(workerRequestDto));

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
    @Transactional
    public WorkerDetailedDto update(Long id, WorkerRequestDto workerRequestDto) {
        log.info("Update worker with id: {} and fields:{}", id, workerRequestDto);

        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(WORKER_NOT_FOUND));
        String oldEmail = worker.getEmail();
        String newEmail = workerRequestDto.getEmail();

        if (!StringUtils.equals(oldEmail, newEmail) && validationService.isEmailExist(newEmail)) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }

        workerMapper.update(workerRequestDto, worker);
        return workerMapper.toDetailedDto(workerRepository.save(worker));
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

        return workerRepository.findAll(createSpecificationForEmployeeHierarchy(workerFilterDto), pageable)
                .map(workerMapper::toDetailedDto);
    }

}
