package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.AuthRequest;
import org.acme.dto.AuthResponse;
import org.acme.entity.User;
import org.acme.security.GenerateToken;
import org.acme.service.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * This class will handle all user sign up/in related http calls.
 */
@Path("/user")
@Tag(name = "Authentication", description = "APIs for managing user Sign Up/In process")
public class Authentication {

    @ConfigProperty(name = "com.transaction.quarkusjwt.jwt.duration")
    public Long duration;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    public String issuer;

    @Inject
    UserService userService;

    /**
     * Endpoint for user sign in.
     *
     * @param authRequest The authentication request containing username and password.
     * @return Response indicating the authentication status and a JWT token for successful authentication.
     */
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "User Sign In")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Success"),
            @APIResponse(responseCode = "401", description = "User is unauthorized"),
            @APIResponse(responseCode = "404", description = "User not found")
    })
    public Response login(@Valid AuthRequest authRequest) {
        User user;
        try {
            user = userService.findUser(authRequest);
            if (user != null && user.getPassword().equals(authRequest.getPassword())) {
                try {
                    return Response.ok(new AuthResponse(GenerateToken.generateToken(user.getUsername(), duration, issuer))).build();
                } catch (Exception e) {
                    return Response.status(Response.Status.UNAUTHORIZED).build();
                }
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint for user sign up.
     *
     * @param authRequest The authentication request containing username and password.
     * @return Response indicating the status of user creation.
     */
    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "User Sign Up")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "User created successfully"),
            @APIResponse(responseCode = "409", description = "User already exists"),
            @APIResponse(responseCode = "500", description = "Failed to add user")
    })
    public Response signup(@Valid AuthRequest authRequest) {
        User user = null;
        try {
            user = userService.findUser(authRequest);
        } catch (NotFoundException e) {
            // We don't want to do anything as the user we are trying to add is not present in the database
        }
        if (user != null) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        try {
            userService.addUser(authRequest);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }
}