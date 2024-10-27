package by.andersen.employee.mapper;

import by.andersen.employee.domain.OtherWorker;
import by.andersen.employee.dto.other_worker.OtherWorkerDetailedDto;
import by.andersen.employee.dto.other_worker.OtherWorkerDto;
import by.andersen.employee.dto.other_worker.OtherWorkerRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OtherWorkerMapper {

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "id", ignore = true)
    OtherWorker toEntity(OtherWorkerRequestDto otherWorkerRequestDto);

    OtherWorkerDetailedDto toDetailedDto(OtherWorker otherWorker);

    OtherWorkerDto toDto(OtherWorker otherWorker);

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "id", ignore = true)
    OtherWorker update(OtherWorkerRequestDto otherWorkerRequestDto, @MappingTarget OtherWorker otherWorker);
}
