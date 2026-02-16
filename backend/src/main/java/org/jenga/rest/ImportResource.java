package org.jenga.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import io.quarkus.logging.Log;

import org.jenga.dto.GitHubIssueDTO;
import org.jenga.dto.ImportReportDTO;
import org.jenga.dto.TicketRequestDTO;
import org.jenga.service.ImportService;

import java.util.List;

@Path("/api/import")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor_ = { @Inject })
public class ImportResource {

    private final ImportService importService;

    @POST
    @Path("/{projectId}/github")
    public Response importFromGitHub(
            @PathParam("projectId") String projectId,
            List<GitHubIssueDTO> githubIssues) {
        Log.info("Received request to import " + githubIssues.size() + " GitHub issues for project: " + projectId);

        ImportReportDTO report = importService.importFromGitHub(projectId, githubIssues);

        Log.info("GitHub import completed for project: " + projectId + ". Success: " + report.getSuccessfulImportCount()
                + ", Failed: " + report.getFailedImports().size());
        return Response.ok(report).build();
    }

    @POST
    @Path("/{projectId}/import")
    public Response importFromJenga(
            @PathParam("projectId") String projectId,
            List<TicketRequestDTO> jengaIssues) {
        Log.info("Received request to import " + jengaIssues.size() + " Jenga tickets for project: " + projectId);

        ImportReportDTO report = importService.importFromJenga(projectId, jengaIssues);

        Log.info("Jenga import completed for project: " + projectId + ". Success: " + report.getSuccessfulImportCount()
                + ", Failed: " + report.getFailedImports().size());
        return Response.ok(report).build();
    }
}