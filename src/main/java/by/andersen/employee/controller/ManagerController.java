package by.andersen.employee.controller;

import by.andersen.employee.dto.manager.ManagerDetailedDto;
import by.andersen.employee.dto.manager.ManagerDto;
import by.andersen.employee.dto.manager.ManagerFilterDto;
import by.andersen.employee.dto.manager.ManagerRequestDto;
import by.andersen.employee.service.ManagerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @PostMapping
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(CREATED)
    public ManagerDetailedDto save(@Valid @RequestBody ManagerRequestDto managerRequestDto) {
        return managerService.save(managerRequestDto);
    }

    @GetMapping("/{email}")
    @ResponseStatus(OK)
    public ManagerDetailedDto get(@PathVariable String email) {
        return managerService.get(email);
    }

    @PutMapping("/{id}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(OK)
    public ManagerDetailedDto update(@PathVariable Long id, @Valid @RequestBody ManagerRequestDto managerRequestDto) {
        return managerService.update(id, managerRequestDto);
    }

    @GetMapping
    @ResponseStatus(OK)
    public Page<ManagerDto> getAll(@PageableDefault Pageable pageable) {
        return managerService.getAll(pageable);
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    public Page<ManagerDetailedDto> search(@PageableDefault Pageable pageable, ManagerFilterDto managerFilterDto) {
        return managerService.search(pageable, managerFilterDto);
    }

    @PatchMapping("/{id}/subordinates")
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(OK)
    public ManagerDetailedDto addSubordinates(@PathVariable Long id, @RequestBody List<Long> employeeIds) {
        return managerService.addSubordinates(id, employeeIds);
    }

    @DeleteMapping("/{id}/subordinates")
    @PreAuthorize(value = "hasRole('ADMIN')")
    @ResponseStatus(OK)
    public ManagerDetailedDto removeSubordinates(@PathVariable Long id, @RequestParam List<Long> employeeIds) {
        return managerService.removeSubordinates(id, employeeIds);
    }

}
