package com.example.du_an_androidd.model.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookRequest {

    @SerializedName("title")
    private String title;

    @SerializedName("isbn")
    private String isbn;

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("year")
    private int year;

    @SerializedName("description")
    private String description;

    @SerializedName("category_id")
    private int categoryId;

    // QUAN TRỌNG: Dạng List để khớp với Server
    @SerializedName("author_ids")
    private List<Integer> authorIds;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("quantity")
    private int quantity;

    // --- 1. CONSTRUCTOR RỖNG (Bắt buộc phải có) ---
    public BookRequest() {
    }

    // --- 2. CONSTRUCTOR ĐẦY ĐỦ ---
    public BookRequest(String title, String isbn, String publisher, int year,
                       int categoryId, List<Integer> authorIds,
                       String imageUrl, int quantity) {
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
        this.year = year;
        this.categoryId = categoryId;
        this.authorIds = authorIds;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    // --- 3. GETTER & SETTER (Bắt buộc để code Fragment không báo lỗi) ---
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public List<Integer> getAuthorIds() { return authorIds; }
    public void setAuthorIds(List<Integer> authorIds) { this.authorIds = authorIds; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}