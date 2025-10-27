package org.jenga.exception;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.jboss.resteasy.reactive.RestResponse;
import jakarta.ws.rs.BadRequestException;

class ExceptionMappers {
    @ServerExceptionMapper
    public RestResponse<ErrorResponse> mapBadRequestException(BadRequestException exception) {
        ErrorResponse errorResponse = new ErrorResponse(
            "BAD_REQUEST",
            exception.getMessage()
        );

        return RestResponse.status(RestResponse.Status.BAD_REQUEST, errorResponse);
    }
}
