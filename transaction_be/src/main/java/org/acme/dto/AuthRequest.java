package org.acme.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO class representing the request for authentication.
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
public class AuthRequest {

    @NotEmpty(message = "Username must not be empty")
    private String username;

    @NotEmpty(message = "Password must not be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    private String password;
}

