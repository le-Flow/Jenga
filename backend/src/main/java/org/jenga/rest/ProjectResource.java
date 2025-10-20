package org.jenga.rest;

import org.jenga.service.ProjectService;
import org.jenga.dto.ProjectDTO;
import org.jenga.dto.CreateProjectDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @Inject
    ProjectService projectService;

    @POST
    public Response createProject(CreateProjectDTO createProjectDTO) {
        projectService.create(createProjectDTO);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public List<ProjectDTO> getAllProjects() {
        return projectService.findAll();
    }

    @GET
    @Path("/{projectName}")
    public ProjectDTO getProjectByName(@PathParam("projectName") String projectName) {
        return projectService.findByName(projectName);
    }

    @PUT
    @Path("/{projectName}")
    public Response updateProject(@PathParam("projectName") String projectName, ProjectDTO projectDTO) {
        projectService.update(projectName, projectDTO);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{projectName}")
    public Response deleteProject(@PathParam("projectName") String projectName) {
        projectService.delete(projectName);
        return Response.noContent().build();
    }
}
