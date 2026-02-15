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
            exception.getMessage()
        );

        return RestResponse.status(RestResponse.Status.BAD_REQUEST, errorResponse);
    }
}
