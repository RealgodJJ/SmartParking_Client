package com.example.realgodjj.parking_system.simulation;


public class Park {

    private int parkId;
    private String parkUid;
    private String parkName;
    private int totalSpaces;
    private int totalAvailable;
    private double parkPrice;
    private double parkNightPrice;

    public Park() {

    }

    public Park(String parkUid, String parkName, int totalSpaces, int totalAvailable, double parkPrice, double parkNightPrice) {
        this.parkUid = parkUid;
        this.parkName = parkName;
        this.totalSpaces = totalSpaces;
        this.totalAvailable = totalAvailable;
        this.parkPrice = parkPrice;
        this.parkNightPrice = parkNightPrice;
    }

    public int getParkId() {
        return parkId;
    }

    public void setParkId(int parkId) {
        this.parkId = parkId;
    }

    public String getParkUid() {
        return parkUid;
    }

    public void setParkUid(String parkUid) {
        this.parkUid = parkUid;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public int getTotalSpaces() {
        return totalSpaces;
    }

    public void setTotalSpaces(int totalSpaces) {
        this.totalSpaces = totalSpaces;
    }

    public int getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(int totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public double getParkPrice() {
        return parkPrice;
    }

    public void setParkPrice(double parkPrice) {
        this.parkPrice = parkPrice;
    }

    public double getParkNightPrice() {
        return parkNightPrice;
    }

    public void setParkNightPrice(double parkNightPrice) {
        this.parkNightPrice = parkNightPrice;
    }
}
