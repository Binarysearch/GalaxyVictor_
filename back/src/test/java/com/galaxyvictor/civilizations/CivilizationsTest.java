package com.galaxyvictor.civilizations;

import static com.jayway.jsonpath.JsonPath.read;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;

import com.galaxyvictor.BaseTest;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.civilization.CivilizationController;
import com.galaxyvictor.servlet.civilization.CivilizationsController;
import com.galaxyvictor.servlet.galaxies.CurrentGalaxyController;
import com.galaxyvictor.servlet.galaxies.GalaxiesController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CivilizationsTest extends BaseTest {

    private CivilizationController civilizationController;
    private CivilizationsController civilizationsController;

    private int civilizationId;
    private int homeworldId;
    private String civilizationName = "Morlocks";

    @BeforeEach
    public void setupCivilizationTest() throws SQLException {

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