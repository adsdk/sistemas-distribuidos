package com.ulbra.model;

public class Server {
    
    private String name;
    private String location;
    private int[] year;
    private boolean active;

    public Server(String serverName, String serverIp, int portListen, int[] yearData) {
        this.name = serverName;
        this.location = serverIp + ":" + portListen;
        this.year = yearData;
        this.active = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int[] getYear() {
        return year;
    }

    public void setYear(int[] year) {
        this.year = year;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}
