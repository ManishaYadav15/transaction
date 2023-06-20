package org.acme.service;

import org.acme.constants.Operation;
import org.acme.entity.TransactionHistory;
import org.acme.entity.Wallet;
import org.acme.dto.WalletTransactionRequest;

import java.util.List;

/**
 * An interface for the wallet service.
 */
public interface WalletService {

    Wallet getBalance(String username);

    Wallet updateBalance(String username, Operation operation, WalletTransactionRequest request);

    List<TransactionHistory> getTransactions(String username);
}
