package com.galaxyvictor.galaxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static com.jayway.jsonpath.JsonPath.read;
import static org.mockito.BDDMockito.given;

import java.sql.SQLException;

import com.galaxyvictor.ContextListener;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.auth.AuthController;
import com.galaxyvictor.servlet.auth.RegisterController;
import com.galaxyvictor.servlet.galaxies.CurrentGalaxyController;
import com.galaxyvictor.servlet.galaxies.GalaxiesController;

import org.junit.Before;
import org.junit.Test;

public class GalaxiesTest {

    private CurrentGalaxyController currentGalaxyController;
    private GalaxiesController galaxiesController;
    private AuthController authController;

    private String token;
    private String galaxyName;
    private int galaxyId;

    @Before
    public void setup() throws SQLException {
        new ContextListener().contextInitialized(null);

        ApiRequest request = mock(ApiRequest.class);

        given(request.jsonPath("$.email")).willReturn("test@email.com");
        given(request.jsonPath("$.password")).willReturn("12345");

        String response = new RegisterController().postRequest(request);

        token = read(response, "$.token");

        currentGalaxyController = new CurrentGalaxyController();
        galaxiesController = new GalaxiesController();
        authController = new AuthController();
    }

    @Test
    public void testAll() throws SQLException {
        getGalaxiesTest();
        selectGalaxyTest();
        authTest();
    }

    public void getGalaxiesTest() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);

        String response = galaxiesController.getRequest(request);

        galaxyId = read(response, "[0].id");
        galaxyName = read(response, "[0].name");

        assertNotNull(galaxyName);
    }

    public void selectGalaxyTest() throws SQLException {

        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);
        given(request.jsonPath("$.id")).willReturn(galaxyId);
        given(request.jsonPath("$.password")).willReturn("12345");

        String response = currentGalaxyController.putRequest(request);
        
        assertEquals((int) read(response, "$.id"), galaxyId);
        assertEquals(read(response, "$.name"), galaxyName);

    }
    
    public void authTest() throws SQLException {

        ApiRequest request = mock(ApiRequest.class);

        given(request.getRequestBody()).willReturn(token);

        String response = authController.postRequest(request);
        
        assertNotNull(read(response, "$.token"));
        assertNotNull(read(response, "$.user.id"));
        assertNotNull(read(response, "$.user.currentGalaxy"));
        assertEquals((int) read(response, "$.user.currentGalaxy.id"), galaxyId);
        assertEquals(read(response, "$.user.email"), "test@email.com");

    }
}