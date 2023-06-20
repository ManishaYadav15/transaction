package org.acme.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.acme.constants.Operation;
import org.acme.entity.TransactionHistory;
import org.acme.entity.Wallet;
import org.acme.dto.WalletTransactionRequest;
import org.acme.service.WalletService;
import org.jboss.logging.Logger;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * Implements the methods for Wallet service.
 */
@ApplicationScoped
public class WalletServiceImpl implements WalletService {

    @Inject
    Logger logger;

    /**
     * Retrieves the balance of a wallet for the specified username.
     *
     * @param username The username associated with the wallet.
     * @return The wallet balance.
     * @throws NotFoundException if the wallet is not found.
     */
    @Override
    public Wallet getBalance(String username) {
        Wallet wallet = Wallet.find("username", username).firstResult();
        if (wallet == null) {
            throw new NotFoundException("Wallet not found");
        }
        logger.info("Retrieved wallet balance successfully");
        return wallet;
    }

    /**
     * Updates the balance of a wallet for the specified username based on the specified operation.
     *
     * @param username  The username associated with the wallet.
     * @param operation The operation to perform on the wallet balance (debit or credit).
     * @param request   The transaction request containing the amount.
     * @return The updated wallet.
     * @throws NotFoundException            if the wallet is not found.
     * @throws BadRequestException          if the operation is debit and the wallet has insufficient balance.
     * @throws InternalServerErrorException if there is an error updating the wallet balance.
     */
    @Override
    @Transactional
    public Wallet updateBalance(String username, Operation operation, WalletTransactionRequest request) {
        Wallet wallet = Wallet.find("username", username).firstResult();
        if (wallet == null) {
            logger.error("Wallet not found");
            throw new NotFoundException("Wallet not found");
        }

        double amount = request.getAmount();
        if (operation == Operation.DEBIT) {
            if (wallet.getBalance() < amount) {
                logger.error("Insufficient balance");
                throw new BadRequestException("Insufficient balance");
            }
            wallet.setBalance(wallet.getBalance() - amount);
        } else {
            wallet.setBalance(wallet.getBalance() + amount);
        }

        try {
            TransactionHistory history = new TransactionHistory(username, amount, operation.name(), new Timestamp(System.currentTimeMillis()));
            history.persist(); // Save history first to make sure we record everything
            wallet.update(); // Persist changes to the database
            logger.info("Wallet balance updated successfully");
            return wallet;
        } catch (Exception e) {
            // Handle any database-related exceptions
            logger.error("Error updating wallet balance", e);
            throw new InternalServerErrorException("Error updating wallet balance", e);
        }
    }

    /**
     * Retrieves the transaction history for the specified username.
     *
     * @param username The username associated with the transactions.
     * @return The transaction history list.
     */
    @Override
    public List<TransactionHistory> getTransactions(String username) {
        List<TransactionHistory> history = TransactionHistory.find("username", username).list();
        if (history == null) {
            logger.error("No transactions history found");
            throw new NotFoundException("No transactions history found");
        }
        Collections.sort(history, (h1, h2) -> h2.getDate().compareTo(h1.getDate()));
        logger.info("Retrieved transaction history successfully");
        return history;
    }
}