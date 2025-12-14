package com.example.du_an_androidd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

// --- CÁC IMPORT MỚI CẦN THÊM ---
import com.example.du_an_androidd.api.ApiClient;
import com.example.du_an_androidd.api.ApiService;
import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.request.LoginRequest;
import com.example.du_an_androidd.model.response.LoginResponse;
import com.example.du_an_androidd.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// ------------------------------

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private TokenManager tokenManager; // Khai báo TokenManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo TokenManager
        tokenManager = new TokenManager(this);

        // [TÙY CHỌN] Kiểm tra nếu đã có Token (đã đăng nhập) thì vào thẳng Main
        if (tokenManager.getToken() != null) {
            goToMainActivity();
            return;
        }

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // Set placeholder text (để test cho nhanh, sau này xóa đi)
        etUsername.setText("admin"); // Sửa lại thành user có thật trong DB của bạn
        etPassword.setText("admin123");

        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    // GỌI HÀM ĐĂNG NHẬP API
                    performLogin(username, password);
                }
            }
        });

        // Register link click listener
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Forgot password click listener
        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- HÀM XỬ LÝ GỌI API ---
    private void performLogin(String username, String password) {
        // 1. Tạo request
        LoginRequest request = new LoginRequest(username, password);
        ApiService apiService = ApiClient.getService(this);

        // 2. Gọi API
        apiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                // 3. Xử lý kết quả trả về
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // --- ĐĂNG NHẬP THÀNH CÔNG ---
                        String token = apiResponse.getData().getToken();

                        // Lưu token vào máy
                        tokenManager.saveToken(token);

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        // Server trả về lỗi logic (VD: Sai pass)
                        Toast.makeText(LoginActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Lỗi HTTP (404, 500...)
                    Toast.makeText(LoginActivity.this, "Lỗi hệ thống hoặc sai thông tin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                // Lỗi mạng, mất mạng, server tắt...
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Đóng Activity này để người dùng không back lại được màn login
    }
}