package by.andersen.employee.mapper;

import by.andersen.employee.domain.Worker;
import by.andersen.employee.dto.WorkerDetailedDto;
import by.andersen.employee.dto.WorkerDto;
import by.andersen.employee.dto.WorkerRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkerMapper {

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "id", ignore = true)
    Worker toEntity(WorkerRequestDto workerRequestDto);

    WorkerDetailedDto toDetailedDto(Worker worker);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Worker update(WorkerRequestDto workerRequestDto, @MappingTarget Worker worker);

    WorkerDto toDto(Worker worker);

}
