package com.example.barbershop;

public class User {
    private String id;
    private String email;
    private String role;
    private String fullname;
    private String password;

    // Constructor mặc định cần thiết cho Firestore
    public User() {
    }

    // Constructor có tham số
    public User(String email, String role, String fullname) {
        this.email = email;
        this.fullname = fullname;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
