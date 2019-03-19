package com.galaxyvictor.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static com.jayway.jsonpath.JsonPath.read;
import static org.mockito.BDDMockito.given;

import java.sql.SQLException;

import com.galaxyvictor.ContextListener;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.auth.AuthController;
import com.galaxyvictor.servlet.auth.LoginController;
import com.galaxyvictor.servlet.auth.RegisterController;

import org.junit.Before;
import org.junit.Test;

public class AuthTest {

    private AuthController authController;
    private LoginController loginController;
    private String token;

    @Before
    public void setup() throws SQLException {
        new ContextListener().contextInitialized(null);

        ApiRequest request = mock(ApiRequest.class);

        given(request.jsonPath("$.email")).willReturn("test@email.com");
        given(request.jsonPath("$.password")).willReturn("12345");

        String response = new RegisterController().postRequest(request);

        token = read(response, "$.token");

        authController = new AuthController();
        loginController = new LoginController();
    }

    @Test
    public void testAll() throws SQLException {
        loginTest();
        authTest();
    }

    public void loginTest() throws SQLException {

        ApiRequest request = mock(ApiRequest.class);

        given(request.jsonPath("$.email")).willReturn("test@email.com");
        given(request.jsonPath("$.password")).willReturn("12345");

        String response = loginController.postRequest(request);
        
        assertNotNull(read(response, "$.token"));
        assertNotNull(read(response, "$.user.id"));
        assertNull(read(response, "$.user.currentGalaxy"));
        assertEquals(read(response, "$.user.email"), "test@email.com");

    }

    public void authTest() throws SQLException {

        ApiRequest request = mock(ApiRequest.class);

        given(request.getRequestBody()).willReturn(token);

        String response = authController.postRequest(request);
        
        assertNotNull(read(response, "$.token"));
        assertNotNull(read(response, "$.user.id"));
        assertNull(read(response, "$.user.currentGalaxy"));
        assertEquals(read(response, "$.user.email"), "test@email.com");

    }

    
}