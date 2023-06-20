package org.acme.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.acme.constants.Operation;
import org.acme.dto.WalletTransactionRequest;
import org.acme.entity.TransactionHistory;
import org.acme.entity.Wallet;
import org.acme.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
class WalletResourceTest {

    @InjectMock
    WalletService walletService;

    private String username;
    private double amount;

    @BeforeEach
    void setUp() {
        username = "john";
        amount = 100.0;
    }

    @Test
    void test_getBalance() {
        Wallet wallet = new Wallet(username, amount);
        when(walletService.getBalance(username)).thenReturn(wallet);

        given()
                .pathParam("username", username)
                .when()
                .get("/wallet/{username}")
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(equalTo(BigDecimal.valueOf(amount).toString()));
    }

    @Test
    void test_getBalance_wallet_not_found() {
        when(walletService.getBalance(username)).thenThrow(new NotFoundException());

        given()
                .pathParam("username", username)
                .when()
                .get("/wallet/{username}")
                .then()
                .assertThat()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void test_transaction() {
        String operation = "credit";
        WalletTransactionRequest request = new WalletTransactionRequest(amount);
        Wallet wallet = new Wallet(username, 100.0);
        when(walletService.updateBalance(any(), any(), any())).thenReturn(wallet);

        given()
                .pathParam("username", username)
                .queryParam("operation", operation)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/wallet/{username}/")
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(equalTo("100.0"));
    }

    @Test
    void test_transaction_unsupported_operation() {
        String operation = "unsupported";
        WalletTransactionRequest request = new WalletTransactionRequest(amount);

        given()
                .pathParam("username", username)
                .queryParam("operation", operation)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/wallet/{username}/")
                .then()
                .assertThat()
                .statusCode(Response.Status.NOT_IMPLEMENTED.getStatusCode())
                .body(equalTo("Unsupported operation"));
    }

    @Test
    void test_getTransactions() {
        List<TransactionHistory> transactionHistory = new ArrayList<>();
        TransactionHistory history = new TransactionHistory(username, 100.0, Operation.CREDIT.name(), new Timestamp(System.currentTimeMillis()));
        transactionHistory.add(history);
        when(walletService.getTransactions(username)).thenReturn(transactionHistory);

        given()
                .pathParam("username", username)
                .when()
                .get("/wallet/{username}/transactions")
                .then()
                .assertThat()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", equalTo(transactionHistory.size()));
    }

    @Test
    void test_getTransactions_history_not_found() {
        when(walletService.getTransactions(username)).thenThrow(new NotFoundException());

        given()
                .pathParam("username", username)
                .when()
                .get("/wallet/{username}/transactions")
                .then()
                .assertThat()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
}
