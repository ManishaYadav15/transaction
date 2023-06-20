package org.acme.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.acme.dto.AuthRequest;
import org.acme.entity.User;
import org.acme.entity.Wallet;
import org.acme.service.UserService;
import org.jboss.logging.Logger;

/**
 * Implements the methods for User service.
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    Logger logger;

    /**
     * Finds a user from database based on the provided authentication request.
     *
     * @param authRequest The authentication request containing the username and password.
     * @return The found user.
     * @throws NotFoundException if the user is not found.
     */
    @Override
    public User findUser(AuthRequest authRequest) {
        String username = authRequest.getUsername();
        String password = authRequest.getPassword();
        User user = User.find("username = ?1 and password = ?2", username, password).firstResult();
        if (user == null) {
            logger.error("User not found");
            throw new NotFoundException("User not found");
        }
        logger.info("User found");
        return user;
    }

    /**
     * Adds a new user in the database based on the provided authentication request.
     *
     * @param authRequest The authentication request containing the username and password.
     * @return The added user.
     * @throws RuntimeException if there is an error adding the user.
     */
    @Override
    @Transactional
    public User addUser(AuthRequest authRequest) {
        User user = new User(authRequest.getUsername(), authRequest.getPassword());
        Wallet wallet = new Wallet(user.getUsername(), 0.0);
        try {
            user.persist();
            wallet.persist();
            logger.info("User added successfully");
            return user;
        } catch (Exception e) {
            logger.error("Failed to add user", e);
            throw new RuntimeException("User not added", e);
        }
    }
}
