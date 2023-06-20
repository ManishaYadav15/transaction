package org.acme.service;

import org.acme.dto.AuthRequest;
import org.acme.entity.User;

/**
 * An interface for the user service.
 */
public interface UserService {

    User findUser(AuthRequest authRequest);

    User addUser(AuthRequest authRequest);
}
