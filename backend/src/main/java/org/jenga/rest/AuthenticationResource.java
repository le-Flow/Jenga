package org.jenga.rest;

import org.jenga.service.AuthenticationService;
import org.jenga.dto.LoginRequestDTO;
import org.jenga.dto.LoginResponseDTO;
import org.jenga.dto.RegisterRequestDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    @Inject
    AuthenticationService authenticationService;

    @POST
    @Path("/register")
    public Response register(RegisterRequestDTO registerRequest) {
            LoginResponseDTO loginResponse = authenticationService.register(registerRequest);
            return Response.ok().entity(loginResponse).build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO loginResponse = authenticationService.login(loginRequest);
            return Response.ok().entity(loginResponse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
        }
    }
}
