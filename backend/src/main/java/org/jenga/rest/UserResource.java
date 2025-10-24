package org.jenga.rest;

import org.jenga.service.UserService;
import org.jenga.dto.UserDTO;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @Path("/{username}")
    public UserDTO getUserByUsername(@PathParam("username") String username) {
        return userService.findByUsername(username);
    }
}
