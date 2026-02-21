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
        Log.infof("Received request to import %d GitHub issues for project: %s", githubIssues.size(), projectId);

        ImportReportDTO report = importService.importFromGitHub(projectId, githubIssues);

        Log.infof("GitHub import completed for project: %s. Success: %d, Failed: %d", projectId,
                report.getSuccessfulImportCount(), report.getFailedImports().size());
        return Response.ok(report).build();
    }

    @POST
    @Path("/{projectId}/import")
    public Response importFromJenga(
            @PathParam("projectId") String projectId,
            List<TicketRequestDTO> jengaIssues) {
        Log.infof("Received request to import %d Jenga tickets for project: %s", jengaIssues.size(), projectId);

        ImportReportDTO report = importService.importFromJenga(projectId, jengaIssues);

        Log.infof("Jenga import completed for project: %s. Success: %d, Failed: %d", projectId,
                report.getSuccessfulImportCount(), report.getFailedImports().size());
        return Response.ok(report).build();
    }
}