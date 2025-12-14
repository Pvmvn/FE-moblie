package com.example.du_an_androidd.model.response;

import com.google.gson.annotations.SerializedName;

public class Fine {
    @SerializedName("id")
    private int id;
    @SerializedName("amount")
    private double amount;
    @SerializedName("reason")
    private String reason;
    @SerializedName("paid")
    private int paid; // 0: chưa trả, 1: đã trả
    @SerializedName("member_name")
    private String memberName;

    public double getAmount() { return amount; }
    public String getReason() { return reason; }
    public boolean isPaid() { return paid == 1; }
    public String getMemberName() { return memberName; }
}