package com.galaxyvictor.websocket;


public interface RequestDispatcher {
    
    public String dispatch(WebSocket webSocket, String message) ;
    
    public void addRequestController(String requestType, RequestController controller);

}