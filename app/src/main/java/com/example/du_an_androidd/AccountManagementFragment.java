package com.example.du_an_androidd;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// --- IMPORT MỚI ---
import com.example.du_an_androidd.api.ApiClient;
import com.example.du_an_androidd.api.ApiService;
import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.response.Member;
import com.example.du_an_androidd.utils.TokenManager;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// -----------------

public class AccountManagementFragment extends Fragment {

    private RecyclerView rvAccounts;
    private Button btnAddAccount;
    private Button btnChangePassword;
    private Button btnLogout;
    private Button btnExam;

    private AccountAdapter accountAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_management, container, false);

        // Ánh xạ View
        rvAccounts = view.findViewById(R.id.rvAccounts);
        btnAddAccount = view.findViewById(R.id.btnAddAccount);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnExam = view.findViewById(R.id.btnExam);

        // 1. Cấu hình RecyclerView với Adapter rỗng trước
        accountAdapter = new AccountAdapter(); // Constructor này đã tạo list rỗng bên trong
        rvAccounts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAccounts.setAdapter(accountAdapter);

        // 2. Gọi API để lấy dữ liệu thật
        loadMembersFromApi();

        // --- SỰ KIỆN CÁC NÚT ---

        btnAddAccount.setOnClickListener(v -> {
            // Mở Dialog hoặc Activity thêm độc giả (Bạn có thể làm sau)
            Toast.makeText(getContext(), "Chức năng thêm đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnChangePassword.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đổi mật khẩu", Toast.LENGTH_SHORT).show();
        });

        // 3. Xử lý Đăng xuất chuẩn
        btnLogout.setOnClickListener(v -> {
            // A. Xóa Token trong máy
            TokenManager tokenManager = new TokenManager(getContext());
            tokenManager.clearToken();

            // B. Chuyển về màn hình Login
            Intent intent = new Intent(getContext(), LoginActivity.class);
            // Cờ này để xóa hết các Activity cũ, ngăn người dùng bấm Back quay lại
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnExam.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Bài thi", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // Hàm gọi API lấy danh sách độc giả
    private void loadMembersFromApi() {
        ApiService apiService = ApiClient.getService(getContext());

        apiService.getMembers().enqueue(new Callback<ApiResponse<List<Member>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Member>>> call, Response<ApiResponse<List<Member>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Member>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // Lấy list member từ Server
                        List<Member> members = apiResponse.getData();

                        // Đổ vào Adapter
                        accountAdapter.setData(members);
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Member>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Đã xóa Class Account nội bộ (static class Account) vì không dùng nữa
}