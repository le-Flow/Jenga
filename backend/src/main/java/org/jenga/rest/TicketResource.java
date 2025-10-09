package org.jenga.rest;

import org.jenga.service.TicketService;
import org.jenga.model.Ticket;

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
    public Response createTicket(@PathParam("projectId") Long projectId, Ticket ticket) {
        ticketService.create(projectId, ticket);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public List<Ticket> getAllTickets(@PathParam("projectId") Long projectId) {
        return ticketService.findAll(projectId);
    }

    @GET
    @Path("/{ticketId}")
    public Ticket getTicketById(@PathParam("projectId") Long projectId, @PathParam("ticketId") Long ticketId) {
        return ticketService.findById(projectId, ticketId);
    }

    @PUT
    @Path("/{ticketId}")
    public Response updateTicket(@PathParam("projectId") Long projectId, @PathParam("ticketId") Long ticketId, Ticket ticket) {
        ticketService.update(projectId, ticketId, ticket);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{ticketId}")
    public Response deleteTicket(@PathParam("projectId") Long projectId, @PathParam("ticketId") Long ticketId) {
        ticketService.delete(projectId, ticketId);
        return Response.noContent().build();
    }
}

