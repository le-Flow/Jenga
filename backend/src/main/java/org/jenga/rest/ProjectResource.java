package org.jenga.rest;

import org.jenga.service.ProjectService;
import org.jenga.model.Project;

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
    public Response createProject(Project project) {
        projectService.create(project);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public List<Project> getAllProjects() {
        return projectService.findAll();
    }

    @GET
    @Path("/{id}")
    public Project getProjectById(@PathParam("id") Long id) {
        return projectService.findById(id);
    }

    @PUT
    @Path("/{id}")
    public Response updateProject(@PathParam("id") Long id, Project project) {
        projectService.update(id, project);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProject(@PathParam("id") Long id) {
        projectService.delete(id);
        return Response.noContent().build();
    }
}
