package com.example.du_an_androidd.model.response;

import com.google.gson.annotations.SerializedName;

public class Author {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("bio")
    private String bio;

    // --- ĐÂY LÀ HÀM BẠN ĐANG THIẾU ---
    public int getId() {
        return id;
    }
    // ---------------------------------

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}