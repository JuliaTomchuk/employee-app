package by.andersen.employee.service;

import by.andersen.employee.dto.ManagerDetailedDto;
import by.andersen.employee.dto.ManagerDto;
import by.andersen.employee.dto.ManagerFilterDto;
import by.andersen.employee.dto.ManagerRequestDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagerService {

    ManagerDetailedDto save(ManagerRequestDto managerRequestDto);

    ManagerDetailedDto update(Long id, ManagerRequestDto managerRequestDto);

    ManagerDetailedDto get(String email);

    Page<ManagerDto> getAll(Pageable pageable);

    Page<ManagerDetailedDto> search(Pageable pageable, ManagerFilterDto managerFilterDto);

    ManagerDetailedDto addSubordinates(Long managerId, List<Long> employeeIds);

    ManagerDetailedDto removeSubordinates(Long managerId, List<Long> employeeIds);

}
