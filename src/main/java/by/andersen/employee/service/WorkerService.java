package by.andersen.employee.service;

import by.andersen.employee.dto.worker.WorkerDetailedDto;
import by.andersen.employee.dto.worker.WorkerDto;
import by.andersen.employee.dto.worker.WorkerFilterDto;
import by.andersen.employee.dto.worker.WorkerRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkerService {

    WorkerDetailedDto save(WorkerRequestDto workerRequestDto);

    WorkerDetailedDto get(String email);

    WorkerDetailedDto update(Long id, WorkerRequestDto workerRequestDto);

    Page<WorkerDto> getAll(Pageable pageable);

    Page<WorkerDetailedDto> search(WorkerFilterDto workerFilterDto, Pageable pageable);

}
