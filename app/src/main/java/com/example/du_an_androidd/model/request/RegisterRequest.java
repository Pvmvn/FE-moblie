package com.example.du_an_androidd.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("username")
    private String username;
    @SerializedName("password")
    private String password;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("role")
    private String role; // "admin" hoặc "librarian"

    public RegisterRequest(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }
}