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
    @Path("/{projectId}")
    public ProjectDTO getProjectById(@PathParam("projectId") Long projectId) {
        return projectService.findById(projectId);
    }

    @PUT
    @Path("/{projectId}")
    public Response updateProject(@PathParam("projectId") Long projectId, ProjectDTO projectDTO) {
        projectService.update(projectId, projectDTO);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{projectId}")
    public Response deleteProject(@PathParam("projectId") Long projectId) {
        projectService.delete(projectId);
        return Response.noContent().build();
    }
}
