package com.example.du_an_androidd.model;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data; // Dữ liệu generic (có thể là User, List<Book>,...)

    // Getter
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}