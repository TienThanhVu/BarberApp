package com.example.barbershop;

public class Barber {
    private  String id;
    private  String name;
    private String storeId;
    private String phoneNumber;

    public Barber(){}

    public Barber(String id, String name, String phoneNumber, String storeId) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.storeId = storeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
