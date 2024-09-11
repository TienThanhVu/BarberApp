package com.example.barbershop;

public class Booking {
    private String id;
    private String branchId;
    private String barberName;
    private String date;
    private String time;
    private String service;
    private String voucher;
    private String address;
    private String userName;

    public Booking() {
    }

    public Booking(String id, String branchId, String barberName, String date, String time, String service, String voucher, String address, String userName) {
        this.branchId = branchId;
        this.barberName = barberName;
        this.date = date;
        this.time = time;
        this.service = service;
        this.voucher = voucher;
        this.address = address;
        this.userName = userName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
