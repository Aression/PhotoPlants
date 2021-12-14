package com.example.photoplants.beans;

public class plantSearchBean {
    private String area;
    private String coverURL;
    private String engName;
    private String name;
    private String plantID;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlantID() {
        return plantID;
    }

    public void setPlantID(String plantID) {
        this.plantID = plantID;
    }

    public plantSearchBean(String area, String coverURL, String engName, String name, String plantID) {
        this.area = area;
        this.coverURL = coverURL;
        this.engName = engName;
        this.name = name;
        this.plantID = plantID;
    }
}
