package com.galaxyvictor.auth;

import static com.jayway.jsonpath.JsonPath.read;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;

import com.galaxyvictor.BaseTest;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.auth.AuthController;
import com.galaxyvictor.servlet.auth.LoginController;

import org.junit.jupiter.api.Test;

public class AuthTest extends BaseTest {


    @Test
    public void loginTest() throws SQLException {

        ApiRequest request = mock(ApiRequest.class);

        given(request.jsonPath("$.email")).willReturn("test@email.com");
        given(request.jsonPath("$.password")).willReturn("12345");

        String response = new LoginController().postRequest(request);
        
        assertNotNull(read(response, "$.token"));
        assertNotNull(read(response, "$.user.id"));
        assertNull(read(response, "$.user.currentGalaxy"));
        assertEquals(read(response, "$.user.email"), "test@email.com");

    }

    @Test
    public void authTest() throws SQLException {

        ApiRequest request = mock(ApiRequest.class);

        given(request.getRequestBody()).willReturn(token);

        String response = new AuthController().postRequest(request);
        
        assertNotNull(read(response, "$.token"));
        assertNotNull(read(response, "$.user.id"));
        assertNull(read(response, "$.user.currentGalaxy"));
        assertEquals(read(response, "$.user.email"), "test@email.com");

    }

    
}