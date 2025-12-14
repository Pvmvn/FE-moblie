package com.example.du_an_androidd.api;

import android.content.Context;
import com.example.du_an_androidd.utils.TokenManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Đổi IP này thành IP máy tính của bạn nếu chạy máy thật (VD: 192.168.1.X:3000)
    // Giữ nguyên 10.0.2.2 nếu chạy trên máy ảo Android Studio
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit = null;

    public static ApiService getService(Context context) {
        if (retrofit == null) {
            TokenManager tokenManager = new TokenManager(context);

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(chain -> {
                String token = tokenManager.getToken();
                Request.Builder newRequest = chain.request().newBuilder();
                if (token != null) {
                    newRequest.addHeader("Authorization", "Bearer " + token);
                }
                return chain.proceed(newRequest.build());
            }).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}