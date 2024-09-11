package com.example.barbershop;

public class Location {
    private String id;
    private String address;
    private String phoneNumber;

    // Constructor mặc định
    public Location() {}

    public Location(String name, String address, String phoneNumber, String id, String imageUrl) {
        this.id = id;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
