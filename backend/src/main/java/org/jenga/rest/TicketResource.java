package org.jenga.rest;

import org.jenga.service.TicketService;
import org.jenga.dto.TicketResponseDTO;
import org.jenga.dto.TicketRequestDTO;
import org.jenga.dto.CommentRequestDTO;
import org.jenga.dto.CommentResponseDTO;
import org.jenga.dto.AcceptanceCriteriaRequestDTO;
import org.jenga.dto.AcceptanceCriteriaResponseDTO;

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
    public TicketResponseDTO createTicket(@PathParam("projectId") String projectId, TicketRequestDTO ticketRequestDTO) {
        return ticketService.create(projectId, ticketRequestDTO);
    }

    @GET
    public List<TicketResponseDTO> getAllTickets(@PathParam("projectId") String projectId) {
        return ticketService.findAll(projectId);
    }

    @GET
    @Path("/{ticketId}")
    public TicketResponseDTO getTicketById(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId) {
        return ticketService.findById(projectId, ticketId);
    }

    @GET
    @Path("/nr/{ticketNumber}")
    public TicketResponseDTO getTicketByNumber(@PathParam("projectId") String projectId, @PathParam("ticketNumber") Long ticketNumber) {
        return ticketService.findByTicketNumber(projectId, ticketNumber);
    }

    @PUT
    @Path("/{ticketId}")
    public TicketResponseDTO updateTicket(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId, TicketRequestDTO ticketDTO) {
        return ticketService.update(projectId, ticketId, ticketDTO);
    }

    @DELETE
    @Path("/{ticketId}")
    public Response deleteTicket(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId) {
        ticketService.delete(projectId, ticketId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{ticketId}/duplicate")
    public TicketResponseDTO duplicateTicket(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId) {
        return ticketService.duplicateTicket(projectId, ticketId);
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
    @Path("/{ticketId}/comments")
    public CommentResponseDTO createComment(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId, CommentRequestDTO commentDTO) {
        return ticketService.createComment(projectId, ticketId, commentDTO);
    }

    @GET
    @Path("/{ticketId}/comments")
    public List<CommentResponseDTO> getAllComments(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId) {
        return ticketService.getAllComments(projectId, ticketId);
    }

    @DELETE
    @Path("/{ticketId}/comments/{commentId}")
    public Response deleteComment(@PathParam("projectId") String projectId, @PathParam("ticketId") Long ticketId, @PathParam("commentId") Long commentId) {
        ticketService.deleteComment(projectId, ticketId, commentId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{ticketId}/acceptance-criteria")
    public AcceptanceCriteriaResponseDTO addAcceptanceCriteria(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId,
            AcceptanceCriteriaRequestDTO request) {
        AcceptanceCriteriaResponseDTO response = ticketService.addAcceptanceCriteria(projectId, ticketId, request);
        return response;
    }

    @GET
    @Path("/{ticketId}/acceptance-criteria")
    public List<AcceptanceCriteriaResponseDTO> getAllAcceptanceCriteria(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId) {
        List<AcceptanceCriteriaResponseDTO> criteriaList = ticketService.getAllAcceptanceCriteria(projectId, ticketId);
        return criteriaList;
    }

    @PUT
    @Path("/{ticketId}/acceptance-criteria/{criteriaId}")
    public AcceptanceCriteriaResponseDTO updateAcceptanceCriteria(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId,
            @PathParam("criteriaId") Long criteriaId,
            AcceptanceCriteriaRequestDTO request) {
        return ticketService.updateAcceptanceCriteria(projectId, ticketId, criteriaId, request);
    }

    @DELETE
    @Path("/{ticketId}/acceptance-criteria/{criteriaId}")
    public Response deleteAcceptanceCriteria(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId,
            @PathParam("criteriaId") Long criteriaId) {
        ticketService.deleteAcceptanceCriteria(projectId, ticketId, criteriaId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{ticketId}/related/{relatedTicketId}")
    public Response AddRelatedTicket(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId,
            @PathParam("relatedTicketId") Long relatedTicketId) {
        ticketService.addRelatedTicket(projectId, ticketId, relatedTicketId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{ticketId}/related/{relatedTicketId}")
    public Response removeRelatedTicket(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId,
            @PathParam("relatedTicketId") Long relatedTicketId) {
        ticketService.removeRelatedTicket(projectId, ticketId, relatedTicketId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @PUT
    @Path("/{ticketId}/block/{blockedTicketId}")
    public Response addBlockingTicket(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId,
            @PathParam("blockedTicketId") Long blockedTicketId) {
        ticketService.addBlockingTicket(projectId, ticketId, blockedTicketId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @DELETE
    @Path("/{ticketId}/block/{blockedTicketId}")
    public Response removeBlockingTicket(
            @PathParam("projectId") String projectId,
            @PathParam("ticketId") Long ticketId,
            @PathParam("blockedTicketId") Long blockedTicketId) {
        ticketService.removeBlockingTicket(projectId, ticketId, blockedTicketId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
