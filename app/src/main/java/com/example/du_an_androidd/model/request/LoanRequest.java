package com.example.du_an_androidd.model.request;

public class LoanRequest {
    private int copy_id;    // Khớp API
    private int member_id;  // Khớp API
    private String due_at;  // Khớp API (Format: "2025-02-01T00:00:00.000Z")

    public LoanRequest(int copy_id, int member_id, String due_at) {
        this.copy_id = copy_id;
        this.member_id = member_id;
        this.due_at = due_at;
    }
}