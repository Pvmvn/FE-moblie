package com.example.du_an_androidd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

// --- IMPORT MỚI ---
import com.example.du_an_androidd.api.ApiClient;
import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.request.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// -----------------

/**
 * Register Activity - Đã kết nối API
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullname;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etPhone;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ View
        etFullname = findViewById(R.id.etFullname);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);

        // Sự kiện click nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = etFullname.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                if (fullname.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    // Gọi hàm đăng ký qua API
                    performRegister(username, password, fullname);
                }
            }
        });
    }

    private void performRegister(String username, String password, String fullname) {
        // 1. Tạo request
        // Lưu ý: API yêu cầu 'role', mình để mặc định là "librarian".
        // Trường 'phone' bị bỏ qua vì API auth/register trong tài liệu không có.
        RegisterRequest request = new RegisterRequest(username, password, fullname, "librarian");

        // 2. Gọi API
        ApiClient.getService(this).register(request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // --- ĐĂNG KÝ THÀNH CÔNG ---
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show();

                        // Chuyển về màn hình Login
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Đóng màn hình đăng ký
                    } else {
                        // Lỗi từ server (VD: Trùng username)
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Lỗi hệ thống (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}