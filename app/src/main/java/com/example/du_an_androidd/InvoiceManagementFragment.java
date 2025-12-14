package com.example.du_an_androidd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.du_an_androidd.model.response.Loan; // Sử dụng Model Loan thay cho Invoice

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// -----------------

/**
 * Invoice Management Fragment
 * Hiển thị danh sách Phiếu mượn (Loans) từ API
 */
public class InvoiceManagementFragment extends Fragment {

    private RecyclerView rvInvoices;
    private InvoiceAdapter invoiceAdapter;

    // Xóa list cũ, sử dụng dữ liệu trong Adapter
    // private List<Invoice> invoiceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invoice_management, container, false);

        rvInvoices = view.findViewById(R.id.rvInvoices);

        // 1. Cấu hình Adapter (Không truyền List vào Constructor nữa)
        invoiceAdapter = new InvoiceAdapter(new InvoiceAdapter.OnInvoiceClickListener() {
            @Override
            public void onViewDetailClick(Loan loan) {
                // Hiển thị thông tin chi tiết phiếu mượn
                String detail = "Sách: " + loan.getBookTitle() + "\n" +
                        "Người mượn: " + loan.getMemberName() + "\n" +
                        "Trạng thái: " + loan.getStatus();
                Toast.makeText(getContext(), detail, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeleteClick(Loan loan) {
                // Trong thực tế, đây có thể là chức năng "Trả sách" hoặc "Hủy phiếu mượn"
                // Cần gọi API DELETE hoặc PUT /loans/return
                Toast.makeText(getContext(), "Chức năng hủy/trả đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        rvInvoices.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInvoices.setAdapter(invoiceAdapter);

        // 2. Gọi API để lấy danh sách phiếu mượn
        loadLoansFromApi();

        return view;
    }

    private void loadLoansFromApi() {
        ApiService apiService = ApiClient.getService(getContext());

        // Gọi API lấy danh sách Loans (truyền null vào status để lấy tất cả)
        // Lưu ý: Nếu ApiService của bạn định nghĩa tham số là @Query("status") String status
        apiService.getLoans(null).enqueue(new Callback<ApiResponse<List<Loan>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Loan>>> call, Response<ApiResponse<List<Loan>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Loan>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // Lấy dữ liệu và đổ vào Adapter
                        List<Loan> loans = apiResponse.getData();
                        invoiceAdapter.setData(loans);
                    } else {
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Loan>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Đã xóa Class Invoice nội bộ vì dùng Model Loan rồi
}