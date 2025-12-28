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

    private Button btnFilterAll, btnFilterActive, btnFilterExpired, btnFilterLocked;
    private List<Member> allMembers = new ArrayList<>();
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_management, container, false);

        rvCustomers = view.findViewById(R.id.rvCustomers);
        etSearch = view.findViewById(R.id.etSearch);
        fabAddCustomer = view.findViewById(R.id.fabAddCustomer);
        tvMemberCount = view.findViewById(R.id.tvMemberCount);

        btnFilterAll = view.findViewById(R.id.btnFilterAll);
        btnFilterActive = view.findViewById(R.id.btnFilterActive);
        btnFilterExpired = view.findViewById(R.id.btnFilterExpired);
        btnFilterLocked = view.findViewById(R.id.btnFilterLocked);

        // --- SỰ KIỆN CLICK VÀO KHÁCH HÀNG ---
        customerAdapter = new CustomerAdapter(member -> {
            showMemberDetailDialog(member);
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
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterList(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Nút Thêm (Truyền null để hiểu là thêm mới)
        fabAddCustomer.setOnClickListener(v -> showAddOrEditMemberDialog(null));

        // Load dữ liệu ban đầu
        updateFilterButtonUI();
        loadMembersFromApi();

        return view;
    }

    // --- DIALOG CHI TIẾT (XEM / SỬA / XÓA) ---
    private void showMemberDetailDialog(Member member) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_member_detail, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        TextView tvTitleName = view.findViewById(R.id.tvTitleName);
        TextView tvDetailCode = view.findViewById(R.id.tvDetailCode);
        TextView tvDetailEmail = view.findViewById(R.id.tvDetailEmail);
        TextView tvDetailPhone = view.findViewById(R.id.tvDetailPhone);
        TextView tvDetailAddress = view.findViewById(R.id.tvDetailAddress);
        Button btnEdit = view.findViewById(R.id.btnEdit);
        Button btnDelete = view.findViewById(R.id.btnDelete);
        Button btnClose = view.findViewById(R.id.btnClose);

        // Hiển thị thông tin an toàn (tránh null)
        tvTitleName.setText(member.getFullName());
        tvDetailCode.setText("Mã TV: " + member.getMemberCode());
        tvDetailEmail.setText("Email: " + (member.getEmail() != null ? member.getEmail() : "---"));
        tvDetailPhone.setText("SĐT: " + (member.getPhone() != null ? member.getPhone() : "---"));
        tvDetailAddress.setText("Đ/c: " + (member.getAddress() != null ? member.getAddress() : "---"));

        // Nút Sửa
        btnEdit.setOnClickListener(v -> {
            dialog.dismiss();
            showAddOrEditMemberDialog(member);
        });

        // Nút Xóa
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Cảnh báo")
                    .setMessage("Xóa khách hàng " + member.getFullName() + "?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        deleteMemberApi(member.getId(), dialog);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // --- DIALOG THÊM / SỬA (ĐÃ FIX LỖI CRASH) ---
    private void showAddOrEditMemberDialog(Member member) {
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

        // Nếu member != null => Chế độ SỬA
        if (member != null) {
            // --- FIX CRASH TẠI ĐÂY: Kiểm tra null trước khi setText ---
            etMemberCode.setText(safeString(member.getMemberCode()));
            etFullName.setText(safeString(member.getFullName()));
            etEmail.setText(safeString(member.getEmail()));
            etPhone.setText(safeString(member.getPhone()));
            etAddress.setText(safeString(member.getAddress()));

            btnSave.setText("Cập nhật");
        }

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String code = etMemberCode.getText().toString().trim();
            String name = etFullName.getText().toString().trim();

            if (code.isEmpty() || name.isEmpty()) {
                Toast.makeText(getContext(), "Cần nhập Mã và Tên", Toast.LENGTH_SHORT).show();
                return;
            }

            MemberRequest request = new MemberRequest(code, name,
                    etEmail.getText().toString(),
                    etPhone.getText().toString(),
                    etAddress.getText().toString());

            if (member == null) {
                // Thêm mới
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
                    @Override public void onFailure(Call<ApiResponse<Member>> call, Throwable t) { Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show(); }
                });
            } else {
                // Cập nhật (Sửa)
                ApiClient.getService(getContext()).updateMember(member.getId(), request).enqueue(new Callback<ApiResponse<Member>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Member>> call, Response<ApiResponse<Member>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            loadMembersFromApi();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<ApiResponse<Member>> call, Throwable t) { Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show(); }
                });
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Hàm an toàn: Trả về chuỗi rỗng nếu null
    private String safeString(String input) {
        return input != null ? input : "";
    }

    private void deleteMemberApi(int memberId, AlertDialog detailDialog) {
        ApiClient.getService(getContext()).deleteMember(memberId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                    if(detailDialog != null) detailDialog.dismiss();
                    loadMembersFromApi();
                } else {
                    Toast.makeText(getContext(), "Không thể xóa: Khách đang mượn sách", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFilter(String filterType) {
        this.currentFilter = filterType;
        updateFilterButtonUI();
        filterList();
    }

    private void updateFilterButtonUI() {
        int activeColor = Color.parseColor("#2563EB");
        int inactiveColor = Color.parseColor("#F1F5F9");
        int activeText = Color.WHITE;
        int inactiveText = Color.BLACK;

        setBtnState(btnFilterAll, inactiveColor, inactiveText);
        setBtnState(btnFilterActive, inactiveColor, inactiveText);
        setBtnState(btnFilterExpired, inactiveColor, inactiveText);
        setBtnState(btnFilterLocked, inactiveColor, inactiveText);

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

    private void filterList() {
        List<Member> filteredList = new ArrayList<>();
        String keyword = etSearch.getText().toString().toLowerCase().trim();
        for (Member m : allMembers) {
            boolean matchesFilter = true;
            if (currentFilter.equals("active")) { matchesFilter = m.isActive(); }
            else if (currentFilter.equals("expired")) { matchesFilter = !m.isActive(); }

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
                if (response.isSuccessful() && response.body() != null) {
                    allMembers = response.body().getData();
                    filterList();
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<Member>>> call, Throwable t) {}
        });
    }
}