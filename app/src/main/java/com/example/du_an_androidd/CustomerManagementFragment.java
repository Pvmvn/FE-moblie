package com.example.du_an_androidd;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.du_an_androidd.api.ApiClient;
import com.example.du_an_androidd.api.ApiService;
import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.request.MemberRequest;
import com.example.du_an_androidd.model.response.Member;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerManagementFragment extends Fragment {

    private RecyclerView rvCustomers;
    private CustomerAdapter customerAdapter;
    private EditText etSearch;
    private FloatingActionButton fabAddCustomer;
    private TextView tvMemberCount;

    // Các nút lọc
    private Button btnFilterAll, btnFilterActive, btnFilterExpired, btnFilterLocked;

    // Dữ liệu gốc và biến lưu trạng thái lọc
    private List<Member> allMembers = new ArrayList<>();
    private String currentFilter = "all"; // all, active, expired, locked

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_management, container, false);

        // Ánh xạ View
        rvCustomers = view.findViewById(R.id.rvCustomers);
        etSearch = view.findViewById(R.id.etSearch);
        fabAddCustomer = view.findViewById(R.id.fabAddCustomer);
        tvMemberCount = view.findViewById(R.id.tvMemberCount);

        btnFilterAll = view.findViewById(R.id.btnFilterAll);
        btnFilterActive = view.findViewById(R.id.btnFilterActive);
        btnFilterExpired = view.findViewById(R.id.btnFilterExpired);
        btnFilterLocked = view.findViewById(R.id.btnFilterLocked);

        // Setup Adapter
        customerAdapter = new CustomerAdapter(member -> {
            Toast.makeText(getContext(), "Chi tiết: " + member.getFullName(), Toast.LENGTH_SHORT).show();
        });
        rvCustomers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomers.setAdapter(customerAdapter);

        // --- XỬ LÝ SỰ KIỆN NÚT LỌC ---
        btnFilterAll.setOnClickListener(v -> setFilter("all"));
        btnFilterActive.setOnClickListener(v -> setFilter("active"));
        btnFilterExpired.setOnClickListener(v -> setFilter("expired"));
        btnFilterLocked.setOnClickListener(v -> setFilter("locked"));

        // Tìm kiếm
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(); // Gọi hàm lọc mỗi khi gõ phím
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Nút Thêm
        fabAddCustomer.setOnClickListener(v -> showAddMemberDialog());

        // Load dữ liệu ban đầu
        updateFilterButtonUI(); // Cập nhật màu nút ban đầu
        loadMembersFromApi();

        return view;
    }

    // Hàm đổi bộ lọc
    private void setFilter(String filterType) {
        this.currentFilter = filterType;
        updateFilterButtonUI();
        filterList();
    }

    // Hàm cập nhật màu sắc các nút lọc
    private void updateFilterButtonUI() {
        int activeColor = Color.parseColor("#2563EB"); // Xanh đậm
        int inactiveColor = Color.parseColor("#F1F5F9"); // Xám nhạt
        int activeText = Color.WHITE;
        int inactiveText = Color.BLACK;

        // Reset tất cả về inactive
        setBtnState(btnFilterAll, inactiveColor, inactiveText);
        setBtnState(btnFilterActive, inactiveColor, inactiveText);
        setBtnState(btnFilterExpired, inactiveColor, inactiveText);
        setBtnState(btnFilterLocked, inactiveColor, inactiveText);

        // Set nút đang chọn thành active
        switch (currentFilter) {
            case "all": setBtnState(btnFilterAll, activeColor, activeText); break;
            case "active": setBtnState(btnFilterActive, activeColor, activeText); break;
            case "expired": setBtnState(btnFilterExpired, activeColor, activeText); break;
            case "locked": setBtnState(btnFilterLocked, activeColor, activeText); break;
        }
    }

    private void setBtnState(Button btn, int bgColor, int textColor) {
        if(btn == null) return;
        btn.setBackgroundColor(bgColor);
        btn.setTextColor(textColor);
    }

    // Logic lọc danh sách (Client side)
    private void filterList() {
        List<Member> filteredList = new ArrayList<>();
        String keyword = etSearch.getText().toString().toLowerCase().trim();

        for (Member m : allMembers) {
            // 1. Lọc theo Tab (Logic giả định vì API chưa có field status chuẩn)
            boolean matchesFilter = true;
            if (currentFilter.equals("active")) {
                matchesFilter = m.isActive();
            } else if (currentFilter.equals("expired")) {
                matchesFilter = !m.isActive(); // Giả sử không active là hết hạn
            } else if (currentFilter.equals("locked")) {
                // Logic check khóa (nếu có)
            }

            // 2. Lọc theo tìm kiếm
            boolean matchesSearch = m.getFullName().toLowerCase().contains(keyword) ||
                    (m.getMemberCode() != null && m.getMemberCode().toLowerCase().contains(keyword));

            if (matchesFilter && matchesSearch) {
                filteredList.add(m);
            }
        }

        customerAdapter.setData(filteredList);
        if(tvMemberCount != null) tvMemberCount.setText("Số lượng: " + filteredList.size());
    }

    private void loadMembersFromApi() {
        ApiClient.getService(getContext()).getMembers().enqueue(new Callback<ApiResponse<List<Member>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Member>>> call, Response<ApiResponse<List<Member>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allMembers = response.body().getData(); // Lưu vào list gốc
                    filterList(); // Hiển thị ra màn hình
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<Member>>> call, Throwable t) {}
        });
    }

    private void showAddMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_member, null);
        builder.setView(dialogView);

        EditText etMemberCode = dialogView.findViewById(R.id.etMemberCode);
        EditText etFullName = dialogView.findViewById(R.id.etFullName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);
        EditText etAddress = dialogView.findViewById(R.id.etAddress);

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String code = etMemberCode.getText().toString().trim();
            String name = etFullName.getText().toString().trim();

            if (code.isEmpty() || name.isEmpty()) {
                Toast.makeText(getContext(), "Nhập mã và tên!", Toast.LENGTH_SHORT).show();
                return;
            }

            MemberRequest request = new MemberRequest(code, name, etEmail.getText().toString(), etPhone.getText().toString(), etAddress.getText().toString());

            ApiClient.getService(getContext()).addMember(request).enqueue(new Callback<ApiResponse<Member>>() {
                @Override
                public void onResponse(Call<ApiResponse<Member>> call, Response<ApiResponse<Member>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                        loadMembersFromApi();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<ApiResponse<Member>> call, Throwable t) {}
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}