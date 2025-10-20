package org.jenga.mapper;

import org.jenga.model.Project;
import org.jenga.dto.ProjectDTO;
import org.jenga.dto.CreateProjectDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "cdi")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectDTO projectToProjectDTO(Project project);

    Project createProjectDTOToProject(CreateProjectDTO createProjectDTO);
}
