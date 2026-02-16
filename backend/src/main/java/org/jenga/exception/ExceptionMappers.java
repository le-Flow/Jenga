package org.jenga.exception;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.RestResponse;
import jakarta.ws.rs.BadRequestException;
import io.quarkus.logging.Log;

class ExceptionMappers {
    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapBadRequestException(BadRequestException exception) {
        Log.warnf("Bad request: %s", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "BAD_REQUEST",
                exception.getMessage());

        return RestResponse.status(RestResponse.Status.BAD_REQUEST, errorResponse);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapTicketNotFoundException(TicketNotFoundException exception) {
        Log.warnf("Ticket not found: %s", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "TICKET_NOT_FOUND",
                exception.getMessage());

        return RestResponse.status(RestResponse.Status.NOT_FOUND, errorResponse);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapUserNotFoundException(UserNotFoundException exception) {
        Log.warnf("User not found: %s", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "USER_NOT_FOUND",
                exception.getMessage());

        return RestResponse.status(RestResponse.Status.NOT_FOUND, errorResponse);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapProjectNotFoundException(ProjectNotFoundException exception) {
        Log.warnf("Project not found: %s", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "PROJECT_NOT_FOUND",
                exception.getMessage());

        return RestResponse.status(RestResponse.Status.NOT_FOUND, errorResponse);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapLabelNotFoundException(LabelNotFoundException exception) {
        Log.warnf("Label not found: %s", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "LABEL_NOT_FOUND",
                exception.getMessage());

        return RestResponse.status(RestResponse.Status.NOT_FOUND, errorResponse);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapCommentNotFoundException(CommentNotFoundException exception) {
        Log.warnf("Comment not found: %s", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "COMMENT_NOT_FOUND",
                exception.getMessage());

        return RestResponse.status(RestResponse.Status.NOT_FOUND, errorResponse);
    }

    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapAcceptanceCriteriaNotFoundException(
            AcceptanceCriteriaNotFoundException exception) {
        Log.warnf("Acceptance Criteria not found: %s", exception.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                "ACCEPTANCE_CRITERIA_NOT_FOUND",
                exception.getMessage());

        return RestResponse.status(RestResponse.Status.NOT_FOUND, errorResponse);
    }
}
