package com.blakflag.parkmycar.model;

/**
 * Created by Amit on 8/19/2017.
 */

public class ParkingRequst {
    public int isBookingConfirm,isBookingOver,isPaid;
    public String date;
    public int hour;
    public float totalPrice;
    public String paymentMethod, requestBy,requesterEmail,requesterContactNo;
    public double requsterLat,requesterLon;
    public String parkingAddress,parkingEmail;
    public double distance;
    public String key;


    public ParkingRequst() {
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRequstBy() {
        return requestBy;
    }

    public void setRequstBy(String requstBy) {
        this.requestBy = requstBy;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public String getRequesterContactNo() {
        return requesterContactNo;
    }

    public void setRequesterContactNo(String requesterContactNo) {
        this.requesterContactNo = requesterContactNo;
    }

    public double getRequsterLat() {
        return requsterLat;
    }

    public void setRequsterLat(double requsterLat) {
        this.requsterLat = requsterLat;
    }

    public double getRequesterLon() {
        return requesterLon;
    }

    public void setRequesterLon(double requesterLon) {
        this.requesterLon = requesterLon;
    }

    public String getParkingAddress() {
        return parkingAddress;
    }

    public void setParkingAddress(String parkingAddress) {
        this.parkingAddress = parkingAddress;
    }

    public String getParkingEmail() {
        return parkingEmail;
    }

    public void setParkingEmail(String parkingEmail) {
        this.parkingEmail = parkingEmail;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
