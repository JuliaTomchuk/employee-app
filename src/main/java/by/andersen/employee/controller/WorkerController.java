package by.andersen.employee.controller;

import by.andersen.employee.dto.worker.WorkerDetailedDto;
import by.andersen.employee.dto.worker.WorkerDto;
import by.andersen.employee.dto.worker.WorkerFilterDto;
import by.andersen.employee.dto.worker.WorkerRequestDto;
import by.andersen.employee.service.WorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workers")
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping
    @ResponseStatus(CREATED)
    @PreAuthorize(value = "hasRole('ADMIN')")
    public WorkerDetailedDto save(@RequestBody @Valid WorkerRequestDto workerRequestDto) {
        return workerService.save(workerRequestDto);
    }

    @GetMapping("/{email}")
    @ResponseStatus(OK)
    public WorkerDetailedDto get(@PathVariable String email) {
        return workerService.get(email);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    @PreAuthorize(value = "hasRole('ADMIN')")
    public WorkerDetailedDto update(@PathVariable Long id,
                                    @Valid @RequestBody WorkerRequestDto workerRequestDto) {
        return workerService.update(id, workerRequestDto);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<WorkerDto> get(@PageableDefault Pageable pageable) {
        return workerService.getAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    public Page<WorkerDetailedDto> search(@PageableDefault Pageable pageable, WorkerFilterDto workerFilterDto) {
        return workerService.search(workerFilterDto, pageable);
    }

}
