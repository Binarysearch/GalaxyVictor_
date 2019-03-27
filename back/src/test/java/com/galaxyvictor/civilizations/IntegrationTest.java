package com.galaxyvictor.civilizations;

import static com.jayway.jsonpath.JsonPath.read;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.galaxyvictor.BaseTest;
import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.civilization.CivilizationController;
import com.galaxyvictor.servlet.civilization.CivilizationsController;
import com.galaxyvictor.servlet.civilization.ColoniesController;
import com.galaxyvictor.servlet.civilization.ColonyBuildingsController;
import com.galaxyvictor.servlet.civilization.ColonyResourcesController;
import com.galaxyvictor.servlet.civilization.ConstantDataController;
import com.galaxyvictor.servlet.fleets.ShipModelsController;
import com.galaxyvictor.servlet.galaxies.CurrentGalaxyController;
import com.galaxyvictor.servlet.galaxies.GalaxiesController;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

@TestMethodOrder(OrderAnnotation.class)
public class IntegrationTest extends BaseTest {

    private static int civilizationId;

    private static long colonyId;
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
        given(colonyBuildingsRequest.getLongParam("colony", 0)).willReturn(colonyId);
        String colonyBuildingsResponse = new ColonyBuildingsController().getRequest(colonyBuildingsRequest);
        
        assertNotNull(read(colonyBuildingsResponse, "[0].id"));
        assertEquals(read(colonyBuildingsResponse, "[0].type"), "imperial capital");
    }

    @Test
    @Order(3)
    public void testGetColonyResources() throws SQLException {

        //Colony buildings
        ApiRequest colonyResourcesRequest = mock(ApiRequest.class);
        given(colonyResourcesRequest.getToken()).willReturn(token);
        given(colonyResourcesRequest.getLongParam("colony", 0)).willReturn(colonyId);
        String colonyResourcesResponse = new ColonyResourcesController().getRequest(colonyResourcesRequest);
        
        assertNotNull(read(colonyResourcesResponse, "[0].type"));
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

    @Test
    @Order(4)
    public void testSetColonyBuildingOrder() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);
        given(request.jsonPath("$.colony")).willReturn((int) colonyId);
        given(request.jsonPath("$.buildingType")).willReturn("wind power plant");

        String response = new ColonyBuildingsController().postRequest(request);
        
        assertEquals(read(response, "$.buildingTypeId"), "wind power plant");
        assertEquals(read(response, "$.name"), "Planta de enrgia eolica");
        assertEquals((int) read(response, "$.colony"), colonyId);
        assertEquals(read(response, "$.costs[0].resourceType"), "iron");
        assertEquals((int) read(response, "$.costs[0].quantity"), 50);
        
    }

    @Test
    @Order(5)
    public void testGetShipModels() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);

        String response = new ShipModelsController().getRequest(request);
        
        assertNotNull(read(response, "[0].id"));
        assertNotNull(read(response, "[0].name"));
        assertNotNull(read(response, "[0].canColonize"));
        assertNotNull(read(response, "[0].canFight"));
        
    }

    @Test
    public void testGetConstantData() throws SQLException {
        ApiRequest request = mock(ApiRequest.class);

        given(request.getToken()).willReturn(token);

        String response = new ConstantDataController().getRequest(request);

        // resource types
        assertNotNull(read(response, "$.resourceTypes[0].id"));
        assertNotNull(read(response, "$.resourceTypes[0].name"));
        
        // building capabilities
        assertNotNull(read(response, "$.colonyBuildingCapabilityTypes[0].id"));
        assertNotNull(read(response, "$.colonyBuildingCapabilityTypes[0].name"));
        

        // building types
        List<Map<String, Object>> buildings = read(response, "$.colonyBuildingTypes");

        // get shipyard position
        int i = 0;
        int s = 0;
        for (Map<String, Object> building : buildings) {
            if(building.get("id").equals("shipyard")){
                s = i;
            }
            i++;
        }
        
        assertEquals(read(response, "$.colonyBuildingTypes["+s+"].id"), "shipyard");
        assertEquals(read(response, "$.colonyBuildingTypes["+s+"].name"), "Astillero espacial");
        assertEquals(read(response, "$.colonyBuildingTypes["+s+"].buildable"), true);
        assertEquals(read(response, "$.colonyBuildingTypes["+s+"].repeatable"), false);
        assertNotNull(read(response, "$.colonyBuildingTypes["+s+"].resources[0].type"));
        assertNotNull(read(response, "$.colonyBuildingTypes["+s+"].resources[0].quantity"));
        assertNotNull(read(response, "$.colonyBuildingTypes["+s+"].costs[0].type"));
        assertNotNull(read(response, "$.colonyBuildingTypes["+s+"].costs[0].quantity"));
        assertEquals(read(response, "$.colonyBuildingTypes["+s+"].capabilities[0].type"), "build ships");
        

    }

}