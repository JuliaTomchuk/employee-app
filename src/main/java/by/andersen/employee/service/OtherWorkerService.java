package by.andersen.employee.service;

import by.andersen.employee.dto.other_worker.OtherWorkerDetailedDto;
import by.andersen.employee.dto.other_worker.OtherWorkerDto;
import by.andersen.employee.dto.other_worker.OtherWorkerFilterDto;
import by.andersen.employee.dto.other_worker.OtherWorkerRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OtherWorkerService {

    OtherWorkerDetailedDto save(OtherWorkerRequestDto otherWorkerRequestDto);

    OtherWorkerDetailedDto get(String email);

    OtherWorkerDetailedDto update(Long id, OtherWorkerRequestDto otherWorkerRequestDto);

    Page<OtherWorkerDto> getAll(Pageable pageable);

    Page<OtherWorkerDetailedDto> search(OtherWorkerFilterDto otherWorkerFilterDto, Pageable pageable);

}
