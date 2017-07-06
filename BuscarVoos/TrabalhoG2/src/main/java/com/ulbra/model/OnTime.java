package com.ulbra.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ontime")
public class OnTime {

    private String id;
    private String year;
    private String dayofMonth;
    private String ArrDelay;
    private String DepDelay;
    private String Origin;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDayofMonth() {
        return dayofMonth;
    }

    public void setDayofMonth(String dayofMonth) {
        this.dayofMonth = dayofMonth;
    }

    public String getArrDelay() {
        return ArrDelay;
    }

    public void setArrDelay(String ArrDelay) {
        this.ArrDelay = ArrDelay;
    }

    public String getDepDelay() {
        return DepDelay;
    }

    public void setDepDelay(String DepDelay) {
        this.DepDelay = DepDelay;
    }

    public String getOrigin() {
        return Origin;
    }

    public void setOrigin(String Origin) {
        this.Origin = Origin;
    }

}
