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

public class ApiClient {
    // 10.0.2.2 là localhost của máy tính khi chạy trên máy ảo Android Studio -> CHUẨN
    private static final String BASE_URL = "http://10.0.2.2:3000/";

    // Lưu biến này để không tạo lại quá nhiều lần nếu không cần thiết
    private static Retrofit retrofit = null;

    public static ApiService getService(Context context) {
        TokenManager tokenManager = new TokenManager(context);

        OkHttpClient client = new OkHttpClient.Builder()
                // Tăng thời gian chờ lên 30s (upload ảnh mạng yếu dễ bị timeout)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    String token = tokenManager.getToken();
                    Request originalRequest = chain.request();
                    Request.Builder newRequest = originalRequest.newBuilder();

                    // --- ĐÃ XÓA ĐOẠN ÉP CONTENT-TYPE ĐỂ TRÁNH LỖI UPLOAD ẢNH ---

                    // Chỉ thêm Token
                    if (token != null && !token.isEmpty()) {
                        newRequest.addHeader("Authorization", "Bearer " + token);
                    }
                    return chain.proceed(newRequest.build());
                })
                .addInterceptor(chain -> {
                    // Log request để debug (Giữ nguyên code của bạn rất tốt)
                    Request request = chain.request();
                    if (request.body() != null) {
                        try {
                            // Cẩn thận: Log body của file upload (binary) có thể làm đơ máy ảo
                            // Chỉ nên log nếu body ngắn hoặc text
                            android.util.Log.d("ApiRequest", "Request URL: " + request.url());
                        } catch (Exception e) {
                            android.util.Log.e("ApiRequest", "Error logging", e);
                        }
                    }

                    Response response = chain.proceed(request);

                    // Log lỗi nếu thất bại
                    if (!response.isSuccessful()) {
                        try {
                            // Copy body để đọc, tránh làm hỏng luồng đọc chính
                            String responseBody = response.peekBody(Long.MAX_VALUE).string();
                            android.util.Log.e("ApiResponse", "LỖI SERVER (" + response.code() + "): " + responseBody);
                        } catch (IOException e) {
                            android.util.Log.e("ApiResponse", "Error reading response body", e);
                        }
                    } else {
                        android.util.Log.d("ApiResponse", "Thành công: " + response.code());
                    }
                    return response;
                })
                .build();

        // Luôn tạo mới để đảm bảo Token mới nhất được nạp vào
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }
}