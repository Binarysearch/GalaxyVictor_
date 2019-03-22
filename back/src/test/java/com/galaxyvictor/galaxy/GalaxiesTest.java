package com.galaxyvictor.galaxy;

import static com.jayway.jsonpath.JsonPath.read;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;

import com.galaxyvictor.BaseTest;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.auth.AuthController;
import com.galaxyvictor.servlet.galaxies.CurrentGalaxyController;
import com.galaxyvictor.servlet.galaxies.GalaxiesController;
import com.galaxyvictor.servlet.galaxies.StarSystemsController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GalaxiesTest extends BaseTest {

    private CurrentGalaxyController currentGalaxyController;
    private GalaxiesController galaxiesController;
    private AuthController authController;
    private StarSystemsController starSystemsController;

    
    private String galaxyName;
    private int galaxyId;

    @BeforeEach
    public void setupGalaxiesTest() throws SQLException {
        

        

        currentGalaxyController = new CurrentGalaxyController();
        galaxiesController = new GalaxiesController();
        authController = new AuthController();
        starSystemsController = new StarSystemsController();
    }

    @Test
    public void testAll() throws SQLException {
        getGalaxiesTest();
        selectGalaxyTest();
        authTestWithGalaxy();
        getStarSystemsTest();
    }

    private void getStarSystemsTest() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);

        String response = starSystemsController.getRequest(request);

        assertNotNull(read(response, "[0].id"));
        assertNotNull(read(response, "[0].x"));
        assertNotNull(read(response, "[0].y"));
        assertNotNull(read(response, "[0].type"));
        assertNotNull(read(response, "[0].size"));

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

        String response = currentGalaxyController.putRequest(request);
        
        assertEquals((int) read(response, "$.id"), galaxyId);
        assertEquals(read(response, "$.name"), galaxyName);

    }
    
    public void authTestWithGalaxy() throws SQLException {

        ApiRequest request = mock(ApiRequest.class);

        given(request.getRequestBody()).willReturn(token);

        String response = authController.postRequest(request);
        
        assertNotNull(read(response, "$.token"));
        assertNotNull(read(response, "$.user.id"));
        assertNotNull(read(response, "$.user.currentGalaxy"));
        assertEquals((int) read(response, "$.user.currentGalaxy.id"), galaxyId);
        assertEquals(read(response, "$.user.currentGalaxy.name"), galaxyName);
        assertEquals(read(response, "$.user.email"), "test@email.com");

    }
}