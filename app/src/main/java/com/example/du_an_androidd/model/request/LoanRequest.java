package com.example.du_an_androidd.model.request;

import com.google.gson.annotations.SerializedName;

public class LoanRequest {
    @SerializedName("copy_id")
    private int copyId;
    
    @SerializedName("member_id")
    private int memberId;
    
    @SerializedName("due_at")
    private String dueAt;

    // Constructor rỗng cho Gson
    public LoanRequest() {
    }

    public LoanRequest(int copyId, int memberId, String dueAt) {
        this.copyId = copyId;
        this.memberId = memberId;
        this.dueAt = dueAt;
    }

    // Getters và Setters
    public int getCopyId() { return copyId; }
    public void setCopyId(int copyId) { this.copyId = copyId; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public String getDueAt() { return dueAt; }
    public void setDueAt(String dueAt) { this.dueAt = dueAt; }
}