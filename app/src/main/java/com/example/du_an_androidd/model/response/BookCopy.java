package com.example.du_an_androidd.model.response;

public class BookCopy {
    private int id;
    private String barcode;
    private String status; // "available", "borrowed"
    private String location;

    public String getBarcode() { return barcode; }
    public String getStatus() { return status; }
    public String getLocation() { return location; }
}