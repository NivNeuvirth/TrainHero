package com.example.trainhero.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String userId;
    private String phone;
    private String email;
    private String name;

    public User() {}

    public User(String phone, String email, String name) {
        this.email = email;
        this.phone = phone;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}