package org.acme.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.WalletTransactionRequest;
import org.acme.entity.TransactionHistory;
import org.acme.entity.Wallet;
import org.acme.service.WalletService;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import java.util.List;

/**
 * This class will handle all wallet related http calls.
 */
@Path("/wallet/{username}")
@Tag(name = "Wallet", description = "APIs for managing wallet balance")
public class WalletResource {

    @Inject
    WalletService walletService;

    /**
     * Get the balance of the wallet for a specific username.
     *
     * @param username The username for which to retrieve the wallet balance.
     * @return Response containing the wallet balance.
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get wallet balance by username")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Success"),
            @APIResponse(responseCode = "404", description = "Wallet not found")
    })
    public Response getBalance(
            @PathParam("username")
            @NotEmpty(message = "Username cannot be empty or null") String username
    ) {
        try {
            Wallet wallet = walletService.getBalance(username);
            return Response.ok(wallet.getBalance()).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }

    /**
     * Credit/Debit the wallet balance for a specific username.
     *
     * @param username The username for which to credit/debit the wallet balance.
     * @param request  The transaction request containing the amount to credit/debit.
     * @return Response containing the updated wallet balance.
     */
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Credit/Debit wallet balance")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Success"),
            @APIResponse(responseCode = "400", description = "Insufficient balance"),
            @APIResponse(responseCode = "404", description = "Wallet not found"),
            @APIResponse(responseCode = "501", description = "Unsupported operation"),
            @APIResponse(responseCode = "500", description = "Something went wrong")
    })
    public Response transaction(
            @PathParam("username")
            @NotEmpty(message = "Username cannot be empty or null") String username,
            @QueryParam("operation")
            @NotEmpty(message = "Operation cannot be empty or null") String operation,
            @Valid WalletTransactionRequest request
    ) {
        try {
            org.acme.constants.Operation op;
            if(operation.equals("credit")) {
                op = org.acme.constants.Operation.CREDIT;
            } else if(operation.equals(("debit"))) {
                op = org.acme.constants.Operation.DEBIT;
            }
            else {
                return Response.status(Response.Status.NOT_IMPLEMENTED).entity("Unsupported operation").build();
            }
            Wallet wallet = walletService.updateBalance(username, op, request);
            return Response.ok(wallet.getBalance()).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    /**
     * Get the transactions list for a specific username.
     *
     * @param username The username for which to retrieve the transactions.
     * @return Response containing the list of TransactionHistory.
     */
    @GET
    @Path("/transactions")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get transaction history by username")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Success"),
            @APIResponse(responseCode = "404", description = "Transaction History Not found"),
    })
    public Response getTransactions(
            @PathParam("username")
            @NotEmpty(message = "Username cannot be empty or null") String username
    ) {
        try {
            List<TransactionHistory> transactionHistory = walletService.getTransactions(username);
            return Response.ok(transactionHistory).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }
}
