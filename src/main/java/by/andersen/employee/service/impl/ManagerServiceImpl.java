package by.andersen.employee.service.impl;

import by.andersen.employee.domain.Employee;
import by.andersen.employee.domain.Manager;
import by.andersen.employee.dto.manager.ManagerDetailedDto;
import by.andersen.employee.dto.manager.ManagerDto;
import by.andersen.employee.dto.manager.ManagerFilterDto;
import by.andersen.employee.dto.manager.ManagerRequestDto;
import by.andersen.employee.exception.DataConflictException;
import by.andersen.employee.exception.NotFoundException;
import by.andersen.employee.mapper.ManagerMapper;
import by.andersen.employee.repository.EmployeeRepository;
import by.andersen.employee.repository.ManagerRepository;
import by.andersen.employee.service.ManagerService;
import by.andersen.employee.service.ValidationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static by.andersen.employee.exception.ErrorMessage.EMAIL_ALREADY_EXISTS;
import static by.andersen.employee.exception.ErrorMessage.MANAGER_NOT_FOUND;
import static by.andersen.employee.utils.SpecificationUtils.createSpecificationForEmployeeHierarchy;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {


    private final ManagerRepository managerRepository;
    private final ValidationService validationService;
    private final ManagerMapper managerMapper;
    private final EmployeeRepository employeeRepository;

    @Override
    public ManagerDetailedDto save(ManagerRequestDto managerRequestDto) {
        log.info("Save manager: {}", managerRequestDto);

        if (validationService.isEmailExist(managerRequestDto.getEmail())) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }
        Manager saved = managerRepository.save(managerMapper.toEntity(managerRequestDto));

        return managerMapper.toManagerDetailedDto(saved);
    }

    @Override
    @Transactional
    public ManagerDetailedDto update(Long id, ManagerRequestDto managerRequestDto) {
        log.info("Update manger with id: {} and fields: {}", id, managerRequestDto);

        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MANAGER_NOT_FOUND));

        String newEmail = managerRequestDto.getEmail();
        String oldEmail = manager.getEmail();
        if (!StringUtils.equals(newEmail, oldEmail) && validationService.isEmailExist(newEmail)) {
            throw new DataConflictException(EMAIL_ALREADY_EXISTS);
        }

        managerMapper.update(managerRequestDto, manager);
        return managerMapper.toManagerDetailedDto(managerRepository.save(manager));
    }

    @Override
    public ManagerDetailedDto get(String email) {
        log.info("Get manager by email: {}", email);

        Manager manager = managerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(MANAGER_NOT_FOUND));

        return managerMapper.toManagerDetailedDto(manager);
    }

    @Override
    public Page<ManagerDto> getAll(Pageable pageable) {
        log.info("Get all managers with pageable: {}", pageable);

        return managerRepository.findAll(pageable)
                .map(managerMapper::toDto);
    }

    @Override
    @Transactional
    public Page<ManagerDetailedDto> search(Pageable pageable, ManagerFilterDto mangerFilterDto) {
        log.info("Search mangers with filter: {} and pageable: {}", mangerFilterDto, pageable);

        return managerRepository.findAll(createSpecificationForEmployeeHierarchy(mangerFilterDto), pageable)
                .map(managerMapper::toManagerDetailedDto);
    }

    @Override
    @Transactional
    public ManagerDetailedDto addSubordinates(Long managerId, List<Long> employeeIds) {
        log.info("Add subordinates with ids: {} to manager with id: {}", employeeIds, managerId);

        validationService.validateSubordinates(managerId, employeeIds);
        Manager manager = get(managerId);
        List<Employee> employeesByIds = employeeRepository.findAllById(employeeIds);
        employeesByIds.forEach(manager::addSubordinate);

        return managerMapper.toManagerDetailedDto(managerRepository.save(manager));
    }

    @Override
    @Transactional
    public ManagerDetailedDto removeSubordinates(Long managerId, List<Long> employeeIds) {
        log.info("Remove subordinates with ids: {} to manager with id: {}", employeeIds, managerId);

        Manager manager = get(managerId);
        List<Employee> subordinates = manager.getSubordinates();
        validationService.validateManagerHasSubordinatesWithIds(subordinates, employeeIds);
        List<Employee> subordinatesToRemove = subordinates.stream().filter(subordinate -> employeeIds.contains(subordinate.getId())).toList();
        subordinatesToRemove.forEach(manager::removeSubordinate);

        return managerMapper.toManagerDetailedDto(managerRepository.save(manager));
    }

    @Override
    @Transactional
    public void deleteManagerFromAllEmployees(Long id) {
        log.info("Delete manager with id: {} from all employees", id);
        employeeRepository.deleteManagerFromEmployees(id);
    }

    private Manager get(Long id) {
        return managerRepository.findEagerById(id)
                .orElseThrow(() -> new NotFoundException(MANAGER_NOT_FOUND));
    }

}
