package com.ulbra.model;

import java.util.ArrayList;
import java.util.List;

public class ListServer {
    
    private List<Server> servers = new ArrayList<>();

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
    
}
