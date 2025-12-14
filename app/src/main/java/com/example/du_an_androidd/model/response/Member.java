package com.example.du_an_androidd.model.response;
import com.google.gson.annotations.SerializedName;

public class Member {
    @SerializedName("id")
    private int id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("member_code")
    private String memberCode;

    @SerializedName("email")
    private String email;

    @SerializedName("phone") // Đây là trường bị thiếu
    private String phone;

    @SerializedName("address")
    private String address;

    @SerializedName("is_active")
    private int isActive;

    // --- CÁC GETTER CẦN BỔ SUNG ---
    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getMemberCode() { return memberCode; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; } // Hàm getPhone() quan trọng
    public String getAddress() { return address; }
    public boolean isActive() { return isActive == 1; }
}