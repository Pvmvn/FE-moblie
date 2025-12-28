package com.example.du_an_androidd.api;

import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.request.*;
import com.example.du_an_androidd.model.response.*;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // --- AUTH ---
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<Object>> register(@Body RegisterRequest request);

    @GET("auth/me")
    Call<ApiResponse<Object>> getCurrentUser();

    // --- BOOKS ---
    @GET("books")
    Call<ApiResponse<List<Book>>> getBooks(@Query("page") int page, @Query("limit") int limit);

    @GET("books")
    Call<ApiResponse<List<Book>>> searchBooks(@Query("search") String keyword);

    @GET("books/{id}")
    Call<ApiResponse<Book>> getBookDetail(@Path("id") int id);

    @POST("books")
    Call<ApiResponse<Book>> addBook(@Body BookRequest request);

    @PUT("books/{id}")
    Call<ApiResponse<Book>> updateBook(@Path("id") int id, @Body BookRequest request);

    @DELETE("books/{id}")
    Call<ApiResponse<Void>> deleteBook(@Path("id") int id);

    // --- MEMBERS (KHÁCH HÀNG) ---
    @GET("members")
    Call<ApiResponse<List<Member>>> getMembers();

    @POST("members")
    Call<ApiResponse<Member>> addMember(@Body MemberRequest request);

    // [QUAN TRỌNG] THÊM 2 HÀM NÀY ĐỂ SỬA LỖI:
    @PUT("members/{id}")
    Call<ApiResponse<Member>> updateMember(@Path("id") int id, @Body MemberRequest request);

    @DELETE("members/{id}")
    Call<ApiResponse<Void>> deleteMember(@Path("id") int id);
    // --------------------------------------------------

    // --- LOANS ---
    @GET("loans")
    Call<ApiResponse<List<Loan>>> getLoans(@Query("status") String status);

    // Nếu bạn muốn lọc theo member_id, có thể dùng hàm này (tùy chọn):
    // @GET("loans")
    // Call<ApiResponse<List<Loan>>> getLoans(@Query("member_id") Integer memberId);

    @POST("loans/borrow")
    Call<ApiResponse<Loan>> borrowBook(@Body LoanRequest request);

    @POST("loans/return")
    Call<ApiResponse<Loan>> returnBook(@Body ReturnRequest returnRequest);

    // --- FINES ---
    @GET("fines")
    Call<ApiResponse<List<Fine>>> getFines(@Query("paid") Integer paid);
}