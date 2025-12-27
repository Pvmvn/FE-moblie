package com.example.du_an_androidd.model.request;

import com.google.gson.annotations.SerializedName;

public class ReturnRequest {
    @SerializedName("loan_id")
    private int loanId;
    
    @SerializedName("returned_at")
    private String returnedAt;

    // Constructor rỗng cho Gson
    public ReturnRequest() {
    }

    public ReturnRequest(int loanId, String returnedAt) {
        this.loanId = loanId;
        this.returnedAt = returnedAt;
    }

    // Getters và Setters
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public String getReturnedAt() { return returnedAt; }
    public void setReturnedAt(String returnedAt) { this.returnedAt = returnedAt; }
}

