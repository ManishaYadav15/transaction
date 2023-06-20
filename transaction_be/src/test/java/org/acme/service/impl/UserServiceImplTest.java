package org.acme.service.impl;

import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.acme.dto.AuthRequest;
import org.acme.entity.User;
import org.acme.entity.Wallet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
class UserServiceImplTest {
    @Inject
    UserServiceImpl userService;

    private User user;
    private AuthRequest authRequest;

    private String username;
    private String password;
    private PanacheQuery query;

    @BeforeEach
    void setUp() {
        username = "john";
        password = "passworD.1234";
        user = Mockito.spy(new User(username, password));
        authRequest = new AuthRequest(username, password);
        query = Mockito.mock(PanacheQuery.class);
        PanacheMock.mock(User.class);
        PanacheMock.mock(Wallet.class);
        when(User.find(anyString(), (Object) any())).thenReturn(query);
    }

    @Test
    void test_findUser() {
        when(query.firstResult()).thenReturn(user);

        assertEquals(user, userService.findUser(authRequest));
    }

    @Test
    void test_findUser_not_found() {
        when(query.firstResult()).thenReturn(null);

        Assertions.assertThrows(NotFoundException.class, () ->
                        userService.findUser(authRequest),
                "User not found");
    }

    @Test
    void test_addUser() {
        User returnedUser = userService.addUser(authRequest);

        assertEquals(username, returnedUser.getUsername());
        assertEquals(password, returnedUser.getPassword());
    }
}
