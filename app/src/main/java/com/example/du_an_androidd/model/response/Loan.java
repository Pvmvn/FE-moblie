package com.example.du_an_androidd.model.response;
import com.google.gson.annotations.SerializedName;

public class Loan {
    @SerializedName("id") // Đây là trường bị thiếu
    private int id;

    @SerializedName("book_title")
    private String bookTitle;

    @SerializedName("member_name")
    private String memberName;

    @SerializedName("borrowed_at")
    private String borrowedAt;

    @SerializedName("due_at")
    private String dueAt;

    @SerializedName("returned_at")
    private String returnedAt;

    @SerializedName("status")
    private String status;

    @SerializedName("book_image_url")
    private String bookImageUrl;

    @SerializedName("book_author")
    private String bookAuthor;

    @SerializedName("member_id")
    private int memberId;

    // --- CÁC GETTER CẦN BỔ SUNG ---
    public int getId() { return id; } // Hàm getId() quan trọng
    public String getBookTitle() { return bookTitle; }
    public String getMemberName() { return memberName; }
    public String getBorrowedAt() { return borrowedAt; }
    public String getDueAt() { return dueAt; }
    public String getReturnedAt() { return returnedAt; }
    public String getStatus() { return status; }
    public String getBookImageUrl() { return bookImageUrl; }
    public String getBookAuthor() { return bookAuthor; }
    public int getMemberId() { return memberId; }
}