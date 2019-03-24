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
import com.galaxyvictor.servlet.civilization.ColoniesController;
import com.galaxyvictor.servlet.civilization.ColonyBuildingsController;
import com.galaxyvictor.servlet.civilization.ColonyResourcesController;
import com.galaxyvictor.servlet.galaxies.CurrentGalaxyController;
import com.galaxyvictor.servlet.galaxies.GalaxiesController;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation.class)
public class ColonyDetailsTest extends BaseTest {

    private static int civilizationId;

    private static int colonyId;
    private static int homeworldId;
    private static String civilizationName = "Psilons";

    @BeforeAll
    public static void setupTest() throws SQLException {
        //MethodOrderer.OrderAnnotation;
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

        //create civilization
        ApiRequest request = mock(ApiRequest.class);
        given(request.getToken()).willReturn(token);
        given(request.jsonPath("$.name")).willReturn(civilizationName);
        given(request.jsonPath("$.homeStarName")).willReturn("Mentar");
        String result = new CivilizationController().postRequest(request);
        civilizationId = read(result, "$.id");
        homeworldId = read(result, "$.homeworld.id");
        
    }

    @Test
    @Order(1)
    public void testGetColony() throws SQLException {


        ApiRequest coloniesRequest = mock(ApiRequest.class);
        given(coloniesRequest.getToken()).willReturn(token);
        String coloniesResponse = new ColoniesController().getRequest(coloniesRequest);
        
        colonyId = (int) read(coloniesResponse, "[0].id");
        assertEquals((int) read(coloniesResponse, "[0].civilization"), civilizationId);
        assertEquals((int) read(coloniesResponse, "[0].planet"), homeworldId);

    }

    @Test
    @Order(2)
    public void testGetColonyBuildings() throws SQLException {

        //Colony buildings
        ApiRequest colonyBuildingsRequest = mock(ApiRequest.class);
        given(colonyBuildingsRequest.getToken()).willReturn(token);
        given(colonyBuildingsRequest.jsonPath("$.colony")).willReturn(colonyId);
        String colonyBuildingsResponse = new ColonyBuildingsController().getRequest(colonyBuildingsRequest);
        
        assertNotNull(read(colonyBuildingsResponse, "[0].id"));
        assertEquals(read(colonyBuildingsResponse, "[0].building_type"), "imperial capital");
    }

    @Test
    @Order(3)
    public void testGetColonyResources() throws SQLException {

        //Colony buildings
        ApiRequest colonyResourcesRequest = mock(ApiRequest.class);
        given(colonyResourcesRequest.getToken()).willReturn(token);
        given(colonyResourcesRequest.jsonPath("$.colony")).willReturn(colonyId);
        String colonyResourcesResponse = new ColonyResourcesController().getRequest(colonyResourcesRequest);
        
        assertNotNull(read(colonyResourcesResponse, "[0].resource_type"));
        assertNotNull(read(colonyResourcesResponse, "[0].quantity"));
        
    }

    @Test
    public void testGetCivilizations() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);

        String response = new CivilizationsController().getRequest(request);

        assertEquals((int) read(response, "[0].id"), civilizationId);
        assertEquals(read(response, "[0].name"), civilizationName);
        assertEquals((int) read(response, "[0].homeworld"), homeworldId);
        
    }
}