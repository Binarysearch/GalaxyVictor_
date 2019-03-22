package com.galaxyvictor.servlet.debug;

import java.util.Collection;

public class SocketConnectionListDTO {

    private long civilization;
    private Collection<String> socketIds;

    public SocketConnectionListDTO(){}

    public SocketConnectionListDTO(long civilization, Collection<String> socketIds){
        this.civilization = civilization;
        this.socketIds = socketIds;
    }

    /**
     * @return the civilization
     */
    public long getCivilization() {
        return civilization;
    }

    /**
     * @param civilization the civilization to set
     */
    public void setCivilization(long civilization) {
        this.civilization = civilization;
    }

    /**
     * @return the socketIds
     */
    public Collection<String> getSocketIds() {
        return socketIds;
    }

    /**
     * @param socketIds the socketIds to set
     */
    public void setSocketIds(Collection<String> socketIds) {
        this.socketIds = socketIds;
    }
}