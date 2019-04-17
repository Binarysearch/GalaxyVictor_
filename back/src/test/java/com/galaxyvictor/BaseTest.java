package com.galaxyvictor;

import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;

import static com.jayway.jsonpath.JsonPath.read;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.galaxyvictor.servlet.ApiRequest;
import com.galaxyvictor.servlet.auth.RegisterController;

import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    private static AtomicBoolean init = new AtomicBoolean(false);

    protected static String token;

    @BeforeAll
    public static void setup() throws SQLException {
        if(!init.getAndSet(true)){
            Config.DEVELOP = true;
            new ContextListener().contextInitialized(null);

            ApiRequest request = mock(ApiRequest.class);

            given(request.jsonPath("$.email")).willReturn("test@email.com");
            given(request.jsonPath("$.password")).willReturn("12345");

            String response = new RegisterController().postRequest(request);

            token = read(response, "$.token");
        }
    }
}