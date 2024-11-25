package by.andersen.employee.service.impl;

import by.andersen.employee.domain.OtherWorker;
import by.andersen.employee.dto.other_worker.OtherWorkerDetailedDto;
import by.andersen.employee.dto.other_worker.OtherWorkerDto;
import by.andersen.employee.dto.other_worker.OtherWorkerFilterDto;
import by.andersen.employee.dto.other_worker.OtherWorkerRequestDto;
import by.andersen.employee.exception.DataConflictException;
import by.andersen.employee.exception.NotFoundException;
import by.andersen.employee.mapper.OtherWorkerMapper;
import by.andersen.employee.repository.OtherWorkerRepository;
import by.andersen.employee.service.OtherWorkerService;
import by.andersen.employee.service.ValidationService;
import by.andersen.employee.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static by.andersen.employee.exception.ErrorMessage.EMAIL_ALREADY_EXISTS;
import static by.andersen.employee.exception.ErrorMessage.OTHER_WORKER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtherWorkerServiceImpl implements OtherWorkerService {

    private final OtherWorkerRepository otherWorkerRepository;
    private final ValidationService validationService;
    private final OtherWorkerMapper otherWorkerMapper;

    @Override
    @Transactional
    public OtherWorkerDetailedDto save(OtherWorkerRequestDto otherWorkerRequestDto) {
        log.info("Save other worker: {}", otherWorkerRequestDto);

        if (validationService.isEmailExist(otherWorkerRequestDto.getEmail())) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }
        OtherWorker saved = otherWorkerRepository.save(otherWorkerMapper.toEntity(otherWorkerRequestDto));

        return otherWorkerMapper.toDetailedDto(saved);
    }

    @Override
    public OtherWorkerDetailedDto get(String email) {
        log.info("Get other worker by email: {}", email);

        OtherWorker otherWorker = otherWorkerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(OTHER_WORKER_NOT_FOUND));

        return otherWorkerMapper.toDetailedDto(otherWorker);
    }

    @Override
    @Transactional
    public OtherWorkerDetailedDto update(Long id, OtherWorkerRequestDto otherWorkerRequestDto) {
        log.info("Update other worker with id: {} and fields: {}", id, otherWorkerRequestDto);

        OtherWorker otherWorker = otherWorkerRepository
                .findById(id).orElseThrow(() -> new NotFoundException(OTHER_WORKER_NOT_FOUND));

        String oldEmail = otherWorker.getEmail();
        String newEmail = otherWorkerRequestDto.getEmail();
        if (!StringUtils.equals(oldEmail, newEmail) && (validationService.isEmailExist(otherWorkerRequestDto.getEmail()))) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }
        otherWorkerMapper.update(otherWorkerRequestDto, otherWorker);
        return otherWorkerMapper.toDetailedDto(otherWorkerRepository.save(otherWorker));
    }

    @Override
    public Page<OtherWorkerDto> getAll(Pageable pageable) {
        log.info("Get all other workers with pageable: {}", pageable);

        return otherWorkerRepository.findAll(pageable)
                .map(otherWorkerMapper::toDto);
    }

    @Override
    public Page<OtherWorkerDetailedDto> search(OtherWorkerFilterDto otherWorkerFilterDto, Pageable pageable) {
        log.info("Search other workers with filter: {} and pageable:{}", otherWorkerFilterDto, pageable);

        return otherWorkerRepository
                .findAll(SpecificationUtils.createSpecificationForEmployeeHierarchy(otherWorkerFilterDto), pageable)
                .map(otherWorkerMapper::toDetailedDto);
    }

}
