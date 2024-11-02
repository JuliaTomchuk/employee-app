package by.andersen.employee.mapper;

import by.andersen.employee.domain.Manager;
import by.andersen.employee.dto.manager.ManagerDetailedDto;
import by.andersen.employee.dto.manager.ManagerDto;
import by.andersen.employee.dto.manager.ManagerRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ManagerMapper {

    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "subordinates", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "id", ignore = true)
    Manager toEntity(ManagerRequestDto managerRequestDto);

    ManagerDetailedDto toManagerDetailedDto(Manager manager);

    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "subordinates", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "id", ignore = true)
    Manager update(ManagerRequestDto managerRequestDto, @MappingTarget Manager manager);

    ManagerDto toDto(Manager manager);
}
