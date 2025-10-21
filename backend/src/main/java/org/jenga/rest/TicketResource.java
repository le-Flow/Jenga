package org.jenga.rest;

import org.jenga.service.TicketService;
import org.jenga.dto.TicketDTO;
import org.jenga.dto.CreateTicketDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/projects/{projectName}/tickets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TicketResource {

    @Inject
    TicketService ticketService;

    @POST
    public Response createTicket(@PathParam("projectName") String projectName, CreateTicketDTO createTicketDTO) {
        ticketService.create(projectName, createTicketDTO);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public List<TicketDTO> getAllTickets(@PathParam("projectName") String projectName) {
        return ticketService.findAll(projectName);
    }

    @GET
    @Path("/id/{ticketId}")
    public TicketDTO getTicketById(@PathParam("projectName") String projectName, @PathParam("ticketId") Long ticketId) {
        return ticketService.findById(projectName, ticketId);
    }

    @GET
    @Path("/{ticketNumber}")
    public TicketDTO getTicketByNumber(@PathParam("projectName") String projectName, @PathParam("ticketNumber") Long ticketNumber) {
        return ticketService.findByTicketNumber(projectName, ticketNumber);
    }

    @PUT
    @Path("/{ticketId}")
    public Response updateTicket(@PathParam("projectName") String projectName, @PathParam("ticketId") Long ticketId, TicketDTO ticketDTO) {
        ticketService.update(projectName, ticketId, ticketDTO);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{ticketId}")
    public Response deleteTicket(@PathParam("projectName") String projectName, @PathParam("ticketId") Long ticketId) {
        ticketService.delete(projectName, ticketId);
        return Response.noContent().build();
    }
}
