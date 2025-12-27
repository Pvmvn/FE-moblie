package com.example.du_an_androidd.model.response;

import com.google.gson.annotations.SerializedName;

public class BookCopy {
    @SerializedName("id")
    private int id;
    
    @SerializedName("barcode")
    private String barcode;
    
    @SerializedName("status")
    private String status; // "available", "borrowed"
    
    @SerializedName("location")
    private String location;

    public int getId() { return id; }
    public String getBarcode() { return barcode; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
}