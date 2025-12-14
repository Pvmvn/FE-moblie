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

    @SerializedName("publisher") // Đây là trường bị thiếu
    private String publisher;

    @SerializedName("year")
    private int year;

    @SerializedName("available_copies")
    private int availableCopies;

    @SerializedName("authors")
    private List<Author> authors;

    // --- CÁC GETTER CẦN BỔ SUNG ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public String getPublisher() { return publisher; } // Hàm getPublisher() quan trọng
    public int getYear() { return year; }
    public int getAvailableCopies() { return availableCopies; }
    public List<Author> getAuthors() { return authors; }
}