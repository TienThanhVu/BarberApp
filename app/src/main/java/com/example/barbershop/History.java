package com.example.barbershop;

public class History {
    private String action; // "Đặt đơn" hoặc "Hủy đơn"
    private String bookingId;
    private String barber;
    private String branch;
    private String date;
    private String time;
    private String service;
    private String voucher;
    private String address;
    private String timestamp; // Thời gian ghi lại hành động này

    public History(){}

    public History(String action, String bookingId, String barber, String branch, String date, String time, String service, String voucher, String address, String timestamp) {
        this.action = action;
        this.bookingId = bookingId;
        this.barber = barber;
        this.branch = branch;
        this.date = date;
        this.time = time;
        this.service = service;
        this.voucher = voucher;
        this.address = address;
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBarber() {
        return barber;
    }

    public void setBarber(String barber) {
        this.barber = barber;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
