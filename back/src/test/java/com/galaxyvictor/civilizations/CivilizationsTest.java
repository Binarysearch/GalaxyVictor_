package com.galaxyvictor.civilizations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static com.jayway.jsonpath.JsonPath.read;
import static org.mockito.BDDMockito.given;

import java.sql.SQLException;

import com.galaxyvictor.ContextListener;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.auth.RegisterController;
import com.galaxyvictor.servlet.civilization.CivilizationController;
import com.galaxyvictor.servlet.civilization.CivilizationsController;
import com.galaxyvictor.servlet.galaxies.CurrentGalaxyController;
import com.galaxyvictor.servlet.galaxies.GalaxiesController;

import org.junit.Before;
import org.junit.Test;

public class CivilizationsTest {

    private String token;
    private CivilizationController civilizationController;
    private CivilizationsController civilizationsController;

    private int civilizationId;
    private int homeworldId;
    private String civilizationName = "Morlocks";

    @Before
    public void setup() throws SQLException {
        new ContextListener().contextInitialized(null);

        //register user
        ApiRequest registerRequest = mock(ApiRequest.class);
        given(registerRequest.jsonPath("$.email")).willReturn("test@email.com");
        given(registerRequest.jsonPath("$.password")).willReturn("12345");
        String registerResponse = new RegisterController().postRequest(registerRequest);
        token = read(registerResponse, "$.token");

        //get galaxies
        ApiRequest getGalaxiesRequest = mock(ApiRequest.class);
        given(getGalaxiesRequest.getToken()).willReturn(token);
        String getGalaxiesResponse = new GalaxiesController().getRequest(getGalaxiesRequest);
        int galaxyId = read(getGalaxiesResponse, "[0].id");

        //set current galaxy
        ApiRequest currentGalaxyRequest = mock(ApiRequest.class);
        given(currentGalaxyRequest.getToken()).willReturn(token);
        given(currentGalaxyRequest.jsonPath("$.id")).willReturn(galaxyId);
        new CurrentGalaxyController().putRequest(currentGalaxyRequest);

        //create civilizations controller
        civilizationController = new CivilizationController();
        civilizationsController = new CivilizationsController();
    }

    @Test
    public void testAll() throws SQLException {
        testCreateCivilization();
        testGetCurrentCivilization();
        testGetCivilizations();
    }

    private void testGetCivilizations() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);

        String response = civilizationsController.getRequest(request);

        assertEquals((int) read(response, "[0].id"), civilizationId);
        assertEquals(read(response, "[0].name"), civilizationName);
        assertEquals((int) read(response, "[0].homeworld"), homeworldId);
        
    }

    private void testCreateCivilization() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);
        given(request.jsonPath("$.name")).willReturn(civilizationName);
        given(request.jsonPath("$.homeStarName")).willReturn("Kahar");

        String response = civilizationController.postRequest(request);

        civilizationId = read(response, "$.id");
        homeworldId = read(response, "$.homeworld.id");
        assertEquals(read(response, "$.name"), civilizationName);
        assertNotNull(read(response, "$.serverTime"));

        assertNotNull(read(response, "$.homeworld.id"));
        assertNotNull(read(response, "$.homeworld.starSystem"));
        assertNotNull(read(response, "$.homeworld.orbit"));
        assertNotNull(read(response, "$.homeworld.type"));
        assertNotNull(read(response, "$.homeworld.size"));

    }

    private void testGetCurrentCivilization() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);

        String response = civilizationController.getRequest(request);

        assertEquals((int) read(response, "$.id"), civilizationId);
        assertEquals(read(response, "$.name"), civilizationName);
        assertNotNull(read(response, "$.serverTime"));
        assertEquals((int) read(response, "$.homeworld.id"), homeworldId);
        assertNotNull(read(response, "$.homeworld.starSystem"));
        assertNotNull(read(response, "$.homeworld.orbit"));
        assertNotNull(read(response, "$.homeworld.type"));
        assertNotNull(read(response, "$.homeworld.size"));
    }
}