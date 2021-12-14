package com.example.photoplants.beans;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class SearchHistory extends SugarRecord {
    @Unique
    String plantName;
    String plantID;// apishop api对应的植物ID
    String searchTime;

    public SearchHistory(){}

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    public String getPlantID() {
        return plantID;
    }

    public void setPlantID(String plantID) {
        this.plantID = plantID;
    }

    public SearchHistory(String plantName, String plantID, String searchTime) {
        this.plantName = plantName;
        this.plantID = plantID;
        this.searchTime = searchTime;
    }

}
