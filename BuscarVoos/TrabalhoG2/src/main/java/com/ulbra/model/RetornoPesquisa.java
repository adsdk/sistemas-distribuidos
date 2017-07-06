package com.ulbra.model;

public class RetornoPesquisa {
    
    private Integer arrivalOnTimeFlights;
    private Integer arrivalDelayedFlights;
    private String arrivalDelayedAverageTime;
    private Integer departureOnTimeFlights;
    private Integer departureDelayedFlights;
    private String departureDelayedAverageTime;

    public Integer getArrivalOnTimeFlights() {
        return arrivalOnTimeFlights;
    }

    public void setArrivalOnTimeFlights(Integer arrivalOnTimeFlights) {
        this.arrivalOnTimeFlights = arrivalOnTimeFlights;
    }

    public Integer getArrivalDelayedFlights() {
        return arrivalDelayedFlights;
    }

    public void setArrivalDelayedFlights(Integer arrivalDelayedFlights) {
        this.arrivalDelayedFlights = arrivalDelayedFlights;
    }

    public String getArrivalDelayedAverageTime() {
        return arrivalDelayedAverageTime;
    }

    public void setArrivalDelayedAverageTime(String arrivalDelayedAverageTime) {
        this.arrivalDelayedAverageTime = arrivalDelayedAverageTime;
    }

    public Integer getDepartureOnTimeFlights() {
        return departureOnTimeFlights;
    }

    public void setDepartureOnTimeFlights(Integer departureOnTimeFlights) {
        this.departureOnTimeFlights = departureOnTimeFlights;
    }

    public Integer getDepartureDelayedFlights() {
        return departureDelayedFlights;
    }

    public void setDepartureDelayedFlights(Integer departureDelayedFlights) {
        this.departureDelayedFlights = departureDelayedFlights;
    }

    public String getDepartureDelayedAverageTime() {
        return departureDelayedAverageTime;
    }

    public void setDepartureDelayedAverageTime(String departureDelayedAverageTime) {
        this.departureDelayedAverageTime = departureDelayedAverageTime;
    }
    
}