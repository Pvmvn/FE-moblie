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

    // Constructor rỗng cho Gson
    public MemberRequest() {
    }

    public MemberRequest(String memberCode, String fullName, String email, String phone, String address) {
        this.memberCode = memberCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters và Setters (có thể cần cho một số trường hợp)
    public String getMemberCode() { return memberCode; }
    public void setMemberCode(String memberCode) { this.memberCode = memberCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}