package com.example.du_an_androidd.model.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookRequest {
    private String title;
    private String isbn;
    private String publisher;
    private int year;
    private String description;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("author_ids")
    private List<Integer> authorIds;

    public BookRequest(String title, String isbn, String publisher, int year, int categoryId, List<Integer> authorIds) {
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
        this.year = year;
        this.categoryId = categoryId;
        this.authorIds = authorIds;
    }
}