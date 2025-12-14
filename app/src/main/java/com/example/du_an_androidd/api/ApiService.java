package com.example.du_an_androidd.api;

import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.request.*; // Import tất cả request
import com.example.du_an_androidd.model.response.*; // Import tất cả response

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<Object>> register(@Body RegisterRequest request);

    // --- BOOKS ---
    @GET("books")
    Call<ApiResponse<List<Book>>> getBooks(@Query("page") int page, @Query("limit") int limit);

    @GET("books")
    Call<ApiResponse<List<Book>>> searchBooks(@Query("search") String keyword);

    @GET("books/{id}")
    Call<ApiResponse<Book>> getBookDetail(@Path("id") int id);

    @POST("books")
    Call<ApiResponse<Book>> addBook(@Body BookRequest request);

    // --- MEMBERS ---
    @GET("members")
    Call<ApiResponse<List<Member>>> getMembers();

    @POST("members")
    Call<ApiResponse<Member>> addMember(@Body MemberRequest request);

    // --- LOANS ---
    @GET("loans")
    Call<ApiResponse<List<Loan>>> getLoans(@Query("status") String status);

    @POST("loans/borrow")
    Call<ApiResponse<Loan>> borrowBook(@Body LoanRequest request);

    @POST("loans/return")
    Call<ApiResponse<Loan>> returnBook(@Body Object returnRequest); // Bạn có thể tạo ReturnRequest nếu cần

    // --- FINES ---
    @GET("fines")
    Call<ApiResponse<List<Fine>>> getFines(@Query("paid") Integer paid); // 0: chưa trả, 1: đã trả
}