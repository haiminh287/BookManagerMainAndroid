package com.example.bookmanager.model;


import com.google.gson.annotations.SerializedName;

public class Order {
    private int id;
    private String name;
    @SerializedName("is_confirm")
    private String state;
    private String email;
    @SerializedName("delivery_address")
    private String address;
    @SerializedName("created_at")
    private String createdAt;



    public Order(int id, String name, String state, String email, String address, String createdAt) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.email = email;
        this.address = address;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
