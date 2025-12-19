package com.example.du_an_androidd.model.response;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Book {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("isbn")
    private String isbn;

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("year")
    private int year;

    @SerializedName("available_copies")
    private int availableCopies;

    @SerializedName("authors")
    private List<Author> authors;

    // --- CÁC TRƯỜNG MỚI ĐƯỢC THÊM (Update Backend) ---
    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("quantity")
    private int quantity;

    // --- CÁC GETTER ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getPublisher() { return publisher; }
    public int getYear() { return year; }
    public int getAvailableCopies() { return availableCopies; }
    public List<Author> getAuthors() { return authors; }

    // --- GETTER CHO TRƯỜNG MỚI ---
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
}