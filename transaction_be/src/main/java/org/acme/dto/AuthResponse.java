package org.acme.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO class representing the response for authentication.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AuthResponse {
    public String token;
}
