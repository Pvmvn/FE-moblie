package com.example.du_an_androidd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import com.example.du_an_androidd.api.ApiClient;
import com.example.du_an_androidd.api.ApiService;
import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.request.LoanRequest;
import com.example.du_an_androidd.model.request.ReturnRequest;
import com.example.du_an_androidd.model.response.Loan;
import com.example.du_an_androidd.model.response.Member;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceManagementFragment extends Fragment {

    private RecyclerView rvLoans;
    private LoanAdapter loanAdapter;
    private FloatingActionButton fabAddLoan;
    private EditText etSearch;
    private TabLayout tabLayout;

    private Button btnFilterAll, btnFilterOverdue, btnFilterDueSoon;

    private List<Loan> allLoans = new ArrayList<>();
    private int currentTab = 0; // 0: Đang mượn, 1: Lịch sử
    private String currentFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loan_management, container, false);

        rvLoans = view.findViewById(R.id.rvLoans);
        fabAddLoan = view.findViewById(R.id.fabAddLoan);
        etSearch = view.findViewById(R.id.etSearch);
        tabLayout = view.findViewById(R.id.tabLayout);

        btnFilterAll = view.findViewById(R.id.btnFilterAll);
        btnFilterOverdue = view.findViewById(R.id.btnFilterOverdue);
        btnFilterDueSoon = view.findViewById(R.id.btnFilterDueSoon);

        loanAdapter = new LoanAdapter(new LoanAdapter.OnLoanClickListener() {
            @Override
            public void onRenewClick(Loan loan) {
                Toast.makeText(getContext(), "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReturnClick(Loan loan) {
                returnBook(loan);
            }
        });

        rvLoans.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLoans.setAdapter(loanAdapter);

        // Setup Tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterList();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Setup Filters
        btnFilterAll.setOnClickListener(v -> setFilter("all"));
        btnFilterOverdue.setOnClickListener(v -> setFilter("overdue"));
        btnFilterDueSoon.setOnClickListener(v -> setFilter("dueSoon"));

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterList(); }
            @Override public void afterTextChanged(Editable s) {}
        });

        fabAddLoan.setOnClickListener(v -> showBorrowBookDialog());

        updateFilterButtonUI();
        loadLoansFromApi();

        return view;
    }

    private void setFilter(String filter) {
        this.currentFilter = filter;
        updateFilterButtonUI();
        filterList();
    }

    private void updateFilterButtonUI() {
        int activeColor = Color.parseColor("#2563EB");
        int inactiveColor = Color.parseColor("#F1F5F9");
        setBtnState(btnFilterAll, currentFilter.equals("all") ? activeColor : inactiveColor, currentFilter.equals("all") ? Color.WHITE : Color.BLACK);
        setBtnState(btnFilterOverdue, currentFilter.equals("overdue") ? activeColor : inactiveColor, currentFilter.equals("overdue") ? Color.WHITE : Color.BLACK);
        setBtnState(btnFilterDueSoon, currentFilter.equals("dueSoon") ? activeColor : inactiveColor, currentFilter.equals("dueSoon") ? Color.WHITE : Color.BLACK);
    }

    private void setBtnState(Button btn, int bgColor, int textColor) {
        if(btn != null) {
            btn.setBackgroundColor(bgColor);
            btn.setTextColor(textColor);
        }
    }

    private void filterList() {
        List<Loan> filtered = new ArrayList<>();
        String keyword = etSearch.getText().toString().toLowerCase().trim();

        for (Loan loan : allLoans) {
            boolean isReturned = (loan.getReturnedAt() != null && !loan.getReturnedAt().isEmpty());
            if (currentTab == 0 && isReturned) continue;
            if (currentTab == 1 && !isReturned) continue;

            if (currentTab == 0 && currentFilter.equals("overdue") && !isOverdue(loan)) continue;

            if (loan.getBookTitle().toLowerCase().contains(keyword) || loan.getMemberName().toLowerCase().contains(keyword)) {
                filtered.add(loan);
            }
        }
        loanAdapter.setData(filtered);
    }

    private boolean isOverdue(Loan loan) {
        if(loan.getDueAt() == null) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date due = sdf.parse(loan.getDueAt());
            return due.before(new Date());
        } catch (Exception e) { return false; }
    }

    private void loadLoansFromApi() {
        ApiClient.getService(getContext()).getLoans(null).enqueue(new Callback<ApiResponse<List<Loan>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Loan>>> call, Response<ApiResponse<List<Loan>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allLoans = response.body().getData();
                    filterList();
                }
            }
            @Override public void onFailure(Call<ApiResponse<List<Loan>>> call, Throwable t) {}
        });
    }

    private void showBorrowBookDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_borrow_book, null);
        builder.setView(dialogView);

        EditText etMemberCode = dialogView.findViewById(R.id.etMemberCode);
        EditText etCopyId = dialogView.findViewById(R.id.etCopyId);
        EditText etDueDate = dialogView.findViewById(R.id.etDueDate);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        etDueDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                String date = year + "-" + (month + 1) + "-" + dayOfMonth; // Format YYYY-M-D
                // Format lại cho chuẩn YYYY-MM-DD
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                cal.set(year, month, dayOfMonth);
                etDueDate.setText(sdf.format(cal.getTime()));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String memberCode = etMemberCode.getText().toString().trim();
            String copyIdStr = etCopyId.getText().toString().trim();
            String dueDate = etDueDate.getText().toString().trim();

            if (memberCode.isEmpty() || copyIdStr.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int copyId;
            try { copyId = Integer.parseInt(copyIdStr); } catch (Exception e) {
                Toast.makeText(getContext(), "ID sách phải là số", Toast.LENGTH_SHORT).show();
                return;
            }

            // GỌI HÀM XỬ LÝ MƯỢN SÁCH
            processBorrowBook(memberCode, copyId, dueDate, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // --- LOGIC MỚI: TÌM ID THÀNH VIÊN TỪ MÃ -> RỒI MỚI MƯỢN ---
    private void processBorrowBook(String memberCode, int copyId, String dueDate, AlertDialog dialog) {
        // B1: Lấy danh sách thành viên để tìm ID từ Code
        ApiClient.getService(getContext()).getMembers().enqueue(new Callback<ApiResponse<List<Member>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Member>>> call, Response<ApiResponse<List<Member>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Member> members = response.body().getData();
                    Integer foundMemberId = null;

                    // Tìm kiếm ID
                    for (Member m : members) {
                        if (m.getMemberCode() != null && m.getMemberCode().equalsIgnoreCase(memberCode)) {
                            foundMemberId = m.getId();
                            break;
                        }
                    }

                    if (foundMemberId != null) {
                        // B2: Có ID rồi -> Gọi API mượn
                        callBorrowApi(copyId, foundMemberId, dueDate, dialog);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy mã thành viên: " + memberCode, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi kiểm tra thành viên", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<Member>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callBorrowApi(int copyId, int memberId, String dueDate, AlertDialog dialog) {
        // Format ngày sang chuẩn ISO nếu cần (ở đây giả sử backend nhận YYYY-MM-DD)
        LoanRequest request = new LoanRequest(copyId, memberId, dueDate);

        ApiClient.getService(getContext()).borrowBook(request).enqueue(new Callback<ApiResponse<Loan>>() {
            @Override
            public void onResponse(Call<ApiResponse<Loan>> call, Response<ApiResponse<Loan>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Mượn sách thành công!", Toast.LENGTH_SHORT).show();
                    loadLoansFromApi(); // Load lại danh sách
                    dialog.dismiss();
                } else {
                    // Backend có thể trả về lỗi nếu sách đã được mượn
                    Toast.makeText(getContext(), "Thất bại: Sách này có thể đang được mượn", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Loan>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnBook(Loan loan) {
        ReturnRequest request = new ReturnRequest(loan.getId(), new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(new Date()));
        ApiClient.getService(getContext()).returnBook(request).enqueue(new Callback<ApiResponse<Loan>>() {
            @Override
            public void onResponse(Call<ApiResponse<Loan>> call, Response<ApiResponse<Loan>> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Đã trả sách", Toast.LENGTH_SHORT).show();
                    loadLoansFromApi();
                } else {
                    Toast.makeText(getContext(), "Lỗi trả sách", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ApiResponse<Loan>> call, Throwable t) {}
        });
    }
}