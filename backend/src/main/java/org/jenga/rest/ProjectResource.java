package org.jenga.rest;

import org.jenga.service.ProjectService;
import org.jenga.dto.ProjectDTO;
import org.jenga.dto.CreateProjectDTO;
import org.jenga.dto.LabelDTO;


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
    public ProjectDTO getProjectByIdentifier(@PathParam("projectId") String projectId) {
        return projectService.findById(projectId);
    }

    @PUT
    @Path("/{projectId}")
    public Response updateProject(@PathParam("projectId") String projectId, ProjectDTO projectDTO) {
        projectService.update(projectId, projectDTO);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{projectId}")
    public Response deleteProject(@PathParam("projectId") String projectId) {
        projectService.delete(projectId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{projectId}/labels")
    public Response createLabel(@PathParam("projectId") String projectId, LabelDTO labelDTO) {
        projectService.createLabel(projectId, labelDTO);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{projectId}/labels")
    public List<LabelDTO> getAllLabels(@PathParam("projectId") String projectId) {
        return projectService.getAllLabels(projectId);
    }

    @DELETE
    @Path("/{projectId}/labels/{labelName}")
    public Response deleteLabel(@PathParam("projectId") String projectId, @PathParam("labelName") String labelName) {
        projectService.deleteLabel(projectId, labelName);
        return Response.noContent().build();
    }
}
