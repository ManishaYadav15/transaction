package org.acme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Positive;

/**
 * DTO class representing a wallet transaction request.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionRequest {

    @Positive(message = "Amount must be a positive value")
    private double amount;
}
