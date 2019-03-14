package com.galaxyvictor.controllers;

import com.galaxyvictor.websocket.Request;
import com.galaxyvictor.websocket.RequestController;
import com.galaxyvictor.websocket.RequireLogin;
import com.galaxyvictor.websocket.Response;

@RequireLogin
public class LolController implements RequestController {

    @Override
    public Response computeResponse(Request request) {
        return new LolResponse("lolresponsecomputed");
    }

}