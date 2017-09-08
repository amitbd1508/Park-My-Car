package com.blakflag.parkmycar.model;

/**
 * Created by Amit on 8/14/2017.
 */

public class ParkingInfo {
    public String contactPerson,address,phone,startTime,endTime,note,submittedBy;
    public double latitude,longitude;
    public  float price;


    public ParkingInfo() {
    }

    public ParkingInfo(String contactPerson, String address, String phone, String startTime, String endTime, String note, double latitude, double longitude, float price) {
        this.contactPerson = contactPerson;
        this.address = address;
        this.phone = phone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;
        this.price = price;
    }
}
