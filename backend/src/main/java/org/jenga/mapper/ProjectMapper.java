package org.jenga.mapper;

import org.jenga.model.Project;
import org.jenga.dto.ProjectDTO;
import org.jenga.dto.CreateProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(source = "id", target = "identifier")
    ProjectDTO projectToProjectDTO(Project project);

    @Mapping(source = "identifier", target = "id")
    Project createProjectDTOToProject(CreateProjectDTO createProjectDTO);
}
