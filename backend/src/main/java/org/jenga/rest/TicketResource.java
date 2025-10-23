package org.jenga.rest;

import org.jenga.service.TicketService;
import org.jenga.dto.TicketDTO;
import org.jenga.dto.CreateTicketDTO;

import org.jenga.dto.ImportStatusDTO;
import org.jenga.dto.GitHubIssueDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/projects/{projectId}/tickets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TicketResource {

    @Inject
    TicketService ticketService;

    @POST
    public Response createTicket(@PathParam("projectId") String projectId, CreateTicketDTO createTicketDTO) {
        ticketService.create(projectId, createTicketDTO);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public List<TicketDTO> getAllTickets(@PathParam("projectId") String projectId) {
        return ticketService.findAll(projectId);
    }

    @GET
    @Path("/{ticketId}")
    public TicketDTO getTicketById(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId) {
        return ticketService.findById(projectId, ticketId);
    }

    @GET
    @Path("/nr/{ticketNumber}")
    public TicketDTO getTicketByNumber(@PathParam("projectId") String projectId, @PathParam("ticketNumber") Long ticketNumber) {
        return ticketService.findByTicketNumber(projectId, ticketNumber);
    }

    @PUT
    @Path("/{ticketId}")
    public Response updateTicket(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId, TicketDTO ticketDTO) {
        ticketService.update(projectId, ticketId, ticketDTO);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{ticketId}")
    public Response deleteTicket(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId) {
        ticketService.delete(projectId, ticketId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{ticketId}/assign")
    public Response assignTicket(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId, @QueryParam("username") String username) {
        ticketService.assignTicket(projectId, ticketId, username);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{ticketId}/unassign")
    public Response unassignTicket(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId) {
        ticketService.unassignTicket(projectId, ticketId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @POST
    @Path("/import/json")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importFromJsonFile(@PathParam("projectId") String projectId, InputStream jsonFileStream) {
        
        List<GitHubIssueDTO> issues;
        try {
            TypeReference<List<GitHubIssueDTO>> typeRef = new TypeReference<>() {};
            issues = objectMapper.readValue(jsonFileStream, typeRef);

        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(new ImportStatusDTO(false, "Failed to parse JSON file: " + e.getMessage()))
                           .build();
        }

        ImportStatusDTO status = ticketService.importFromGitHub(projectId, issues);

        return Response.ok(status).build();
    }
}