package org.acme.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.acme.constants.Operation;
import org.acme.dto.WalletTransactionRequest;
import org.acme.entity.TransactionHistory;
import org.acme.entity.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@QuarkusTest
class WalletServiceImplTest {

    @Inject
    WalletServiceImpl walletService;
    private Wallet wallet;
    private String username;
    private double amount;
    private PanacheQuery query;

    @Captor
    ArgumentCaptor<Double> amountArgumentCaptor;

    @BeforeEach
    void setUp() {
        username = "john";
        amount = 100.0;
        wallet = Mockito.spy(new Wallet(username, amount));
        query = Mockito.mock(PanacheQuery.class);
        amountArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        PanacheMock.mock(Wallet.class);
        PanacheMock.mock(TransactionHistory.class);
        when(Wallet.find(anyString(), (Object) any())).thenReturn(query);
    }

    @Test
    void test_getBalance() {
        when(query.firstResult()).thenReturn(wallet);

        assertEquals(wallet, walletService.getBalance(username));
    }

    @Test
    void test_getBalance_wallet_not_found() {
        when(query.firstResult()).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () ->
                        walletService.getBalance(username),
                "Wallet not found");
    }

    @Test
    void test_updateBalance_debit_operation() {
        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(10.0);
        when(query.firstResult()).thenReturn(wallet);
        doNothing().when(wallet).setBalance(amountArgumentCaptor.capture());
        doNothing().when(wallet).update();

        walletService.updateBalance(username, Operation.DEBIT, walletTransactionRequest);

        double returnedAmount = amountArgumentCaptor.getValue();
        Mockito.verify(wallet, times(1)).setBalance(returnedAmount);
        assertEquals(amount-10.0, returnedAmount);
    }

    @Test
    void test_updateBalance_credit_operation() {
        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(10.0);
        when(query.firstResult()).thenReturn(wallet);
        doNothing().when(wallet).setBalance(amountArgumentCaptor.capture());
        doNothing().when(wallet).update();

        walletService.updateBalance(username, Operation.CREDIT, walletTransactionRequest);

        double returnedAmount = amountArgumentCaptor.getValue();
        Mockito.verify(wallet, times(1)).setBalance(returnedAmount);
        assertEquals(amount+10.0, returnedAmount);
    }

    @Test
    void test_updateBalance_debit_operation_insufficient_balance() {
        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(150.0);
        when(query.firstResult()).thenReturn(wallet);

        Assertions.assertThrows(BadRequestException.class, () ->
                        walletService.updateBalance(username, Operation.DEBIT, walletTransactionRequest),
                "Insufficient balance");
    }

    @Test
    void test_updateBalance_Internal_server_error() {
        WalletTransactionRequest walletTransactionRequest = new WalletTransactionRequest(10.0);
        when(query.firstResult()).thenReturn(wallet);
        doNothing().when(wallet).setBalance(amountArgumentCaptor.capture());
        doThrow(new InternalServerErrorException()).when(wallet).update();

        Assertions.assertThrows(InternalServerErrorException.class, () ->
                        walletService.updateBalance(username, Operation.DEBIT, walletTransactionRequest),
                "Error updating wallet balance");
    }

    @Test
    void test_getTransactions() {
        TransactionHistory transactionHistory = new TransactionHistory(username, 10.0, "credit", new Date());
        List<TransactionHistory> transactionHistoryList = new ArrayList<>();
        transactionHistoryList.add(transactionHistory);
        when(TransactionHistory.find(anyString(), (Object) any())).thenReturn(query);
        when(query.list()).thenReturn(transactionHistoryList);

        assertEquals(transactionHistoryList, walletService.getTransactions(username));
    }

    @Test
    void test_getTransactions_no_history() {
        when(TransactionHistory.find(anyString(), (Object) any())).thenReturn(query);
        when(query.list()).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () ->
                walletService.getTransactions(username),
                "No transactions history found");
    }
}
