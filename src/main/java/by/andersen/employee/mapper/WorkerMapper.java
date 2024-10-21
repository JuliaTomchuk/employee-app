package by.andersen.employee.mapper;

import by.andersen.employee.domain.Worker;
import by.andersen.employee.dto.WorkerDetailedDto;
import by.andersen.employee.dto.WorkerDto;
import by.andersen.employee.dto.WorkerEditDto;
import by.andersen.employee.dto.WorkerSaveDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkerMapper {

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "id", ignore = true)
    Worker toEntity(WorkerSaveDto workerSaveDto);

    WorkerDetailedDto toDetailedDto(Worker worker);

    @Mapping(target = "manager", ignore = true)
    Worker update(WorkerEditDto workerEditDto, @MappingTarget Worker worker);

    WorkerDto toDto(Worker worker);

}
