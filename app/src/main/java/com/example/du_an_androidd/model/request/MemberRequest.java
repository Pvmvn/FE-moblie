package com.example.du_an_androidd.model.request;

import com.google.gson.annotations.SerializedName;

public class MemberRequest {
    @SerializedName("member_code")
    private String memberCode;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;

    public MemberRequest(String memberCode, String fullName, String email, String phone, String address) {
        this.memberCode = memberCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}