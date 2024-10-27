package by.andersen.employee.controller;

import by.andersen.employee.dto.other_worker.OtherWorkerDetailedDto;
import by.andersen.employee.dto.other_worker.OtherWorkerDto;
import by.andersen.employee.dto.other_worker.OtherWorkerFilterDto;
import by.andersen.employee.dto.other_worker.OtherWorkerRequestDto;
import by.andersen.employee.service.OtherWorkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/other-workers")
@RequiredArgsConstructor
public class OtherWorkerController {

    private final OtherWorkerService otherWorkerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OtherWorkerDetailedDto save(@Valid @RequestBody OtherWorkerRequestDto otherWorkerRequestDto) {
        return otherWorkerService.save(otherWorkerRequestDto);
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public OtherWorkerDetailedDto get(@PathVariable String email) {
        return otherWorkerService.get(email);
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public OtherWorkerDetailedDto update(@PathVariable Long id,
                                         @Valid @RequestBody OtherWorkerRequestDto otherWorkerRequestDto) {
        return otherWorkerService.update(id, otherWorkerRequestDto);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<OtherWorkerDto> getAll(@PageableDefault Pageable pageable) {
        return otherWorkerService.getAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    public Page<OtherWorkerDetailedDto> search(OtherWorkerFilterDto otherWorkerFilterDto,
                                               @PageableDefault Pageable pageable) {
        return otherWorkerService.search(otherWorkerFilterDto, pageable);
    }

}
