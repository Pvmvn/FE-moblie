package com.example.du_an_androidd.api;

import android.content.Context;
import com.example.du_an_androidd.utils.TokenManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit; // Thêm import này để set timeout
import java.io.IOException;

/**
 * API CLIENT - Cấu hình Retrofit để kết nối với Backend
 * 
 * TÁC DỤNG:
 * - Tạo Retrofit instance với BASE_URL
 * - Tự động thêm JWT Token vào mọi request (qua Interceptor)
 * - Set timeout để tránh app bị đơ khi mạng chậm
 * - Log requests/responses để debug
 * 
 * KIẾN THỨC ÁP DỤNG:
 * - Retrofit: HTTP client để gọi API
 * - OkHttpClient: HTTP client với interceptors
 * - Interceptor: Tự động thêm headers (Authorization: Bearer token)
 * - GsonConverterFactory: Convert JSON ↔ Java objects tự động
 */
public class ApiClient {
    // 10.0.2.2 là localhost của máy tính khi chạy trên máy ảo Android Studio -> CHUẨN
    // Nếu test trên thiết bị thật, đổi thành IP máy tính (VD: http://192.168.1.100:3000/)
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    // Lưu biến này để không tạo lại quá nhiều lần nếu không cần thiết
    private static Retrofit retrofit = null;

    /**
     * Tạo ApiService instance với cấu hình đầy đủ
     * 
     * @param context Context để lấy Token từ SharedPreferences
     * @return ApiService instance để gọi API
     */
    public static ApiService getService(Context context) {
        TokenManager tokenManager = new TokenManager(context);

        // Tạo OkHttpClient với các tính năng:
        OkHttpClient client = new OkHttpClient.Builder()
                // 1. Timeout: Tránh app bị đơ khi mạng chậm (30 giây)
                .connectTimeout(30, TimeUnit.SECONDS)  // Thời gian chờ kết nối
                .readTimeout(30, TimeUnit.SECONDS)     // Thời gian chờ đọc response
                .writeTimeout(30, TimeUnit.SECONDS)      // Thời gian chờ gửi request
                
                // 2. Interceptor 1: Tự động thêm JWT Token vào mọi request
                .addInterceptor(chain -> {
                    // Lấy token từ SharedPreferences
                    String token = tokenManager.getToken();
                    Request originalRequest = chain.request();
                    Request.Builder newRequest = originalRequest.newBuilder();

                    // Thêm header Authorization nếu có token
                    if (token != null && !token.isEmpty()) {
                        newRequest.addHeader("Authorization", "Bearer " + token);
                    }
                    return chain.proceed(newRequest.build());
                })
                // 3. Interceptor 2: Log requests/responses để debug
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    
                    // Log URL của request
                    android.util.Log.d("ApiRequest", "Request URL: " + request.url());
                    
                    // Gửi request và nhận response
                    Response response = chain.proceed(request);

                    // Log kết quả
                    if (!response.isSuccessful()) {
                        // Lỗi: Log response body để debug
                        try {
                            String responseBody = response.peekBody(Long.MAX_VALUE).string();
                            android.util.Log.e("ApiResponse", "LỖI SERVER (" + response.code() + "): " + responseBody);
                        } catch (IOException e) {
                            android.util.Log.e("ApiResponse", "Error reading response body", e);
                        }
                    } else {
                        // Thành công: Log status code
                        android.util.Log.d("ApiResponse", "Thành công: " + response.code());
                    }
                    return response;
                })
                .build();

        // Tạo Retrofit instance với:
        // - BASE_URL: Địa chỉ server Backend
        // - OkHttpClient: Client với interceptors (token, logging)
        // - GsonConverterFactory: Tự động convert JSON ↔ Java objects
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}