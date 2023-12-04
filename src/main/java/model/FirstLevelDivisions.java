package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;

public class FirstLevelDivisions {
    private int divisionId;
    private String divisionName;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdated;
    private String lastUpdatedBy;
    private int countryId;

    private static ObservableList<FirstLevelDivisions> firstLevelDivisions = FXCollections.observableArrayList();

    public FirstLevelDivisions(int divisionId, String division, Timestamp createDate, String createdBy, Timestamp lastUpdated, String lastUpdatedBy, int countryId){
        this.divisionId = divisionId;
        this.divisionName = division;
        this.createDate = createDate;
        this.createdBy= createdBy;
        this.lastUpdated = lastUpdated;
        this.lastUpdatedBy = lastUpdatedBy;
        this.countryId = countryId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public int getCountryId() {
        return countryId;
    }

    public static void addFirstLevelDivision(FirstLevelDivisions newDivision){
        firstLevelDivisions.add(newDivision);
    }

    public static ObservableList<FirstLevelDivisions> getFirstLevelDivisions(){
        return firstLevelDivisions;
    }

    @Override
    public String toString(){
        return (divisionName);
    }
}
