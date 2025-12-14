package com.example.du_an_androidd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.du_an_androidd.model.response.Book;
import com.example.du_an_androidd.model.response.Fine;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
// -----------------

/**
 * Statistics Fragment
 * Hiển thị thống kê Sách và Doanh thu (Tiền phạt)
 */
public class StatisticsFragment extends Fragment {

    private RecyclerView rvTopBooks;
    private TextView tvDailyRevenue;
    private TextView tvMonthlyRevenue;
    private TextView tvYearlyRevenue;
    private TopBookAdapter topBookAdapter;
    private List<TopBook> topBookList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        rvTopBooks = view.findViewById(R.id.rvTopBooks);
        tvDailyRevenue = view.findViewById(R.id.tvDailyRevenue);
        tvMonthlyRevenue = view.findViewById(R.id.tvMonthlyRevenue);
        tvYearlyRevenue = view.findViewById(R.id.tvYearlyRevenue);

        // Khởi tạo list
        topBookList = new ArrayList<>();

        // Setup RecyclerView
        topBookAdapter = new TopBookAdapter(topBookList);
        rvTopBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTopBooks.setAdapter(topBookAdapter);

        // GỌI API ĐỂ LẤY DỮ LIỆU THỰC TẾ
        loadStatisticsData();

        return view;
    }

    private void loadStatisticsData() {
        ApiService apiService = ApiClient.getService(getContext());

        // 1. Lấy danh sách Sách để hiển thị vào Top Books
        // (Tạm thời lấy 10 cuốn đầu tiên làm "Top")
        apiService.getBooks(1, 10).enqueue(new Callback<ApiResponse<List<Book>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Book>>> call, Response<ApiResponse<List<Book>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body().getData();
                    if (books != null) {
                        topBookList.clear();
                        for (Book b : books) {
                            // Map từ Book model sang TopBook model dùng cho Adapter cũ
                            // Dùng available_copies làm số lượng giả lập
                            topBookList.add(new TopBook(b.getTitle(), b.getPublisher(), b.getAvailableCopies()));
                        }
                        topBookAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Book>>> call, Throwable t) {
                // Ignore error for statistics
            }
        });

        // 2. Tính tổng tiền phạt đã thu (Revenue)
        // Gọi API lấy danh sách phạt đã đóng (paid = 1)
        apiService.getFines(1).enqueue(new Callback<ApiResponse<List<Fine>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Fine>>> call, Response<ApiResponse<List<Fine>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Fine> fines = response.body().getData();

                    double totalRevenue = 0;
                    if (fines != null) {
                        for (Fine fine : fines) {
                            totalRevenue += fine.getAmount();
                        }
                    }

                    // Hiển thị tổng tiền
                    String revenueText = String.format("%,.0f VNĐ", totalRevenue);

                    // Vì API chưa chia theo ngày/tháng, tạm thời hiển thị Tổng cho cả 3 ô
                    // Hoặc bạn có thể để 0 nếu muốn
                    tvDailyRevenue.setText("0 VNĐ");
                    tvMonthlyRevenue.setText("0 VNĐ");
                    tvYearlyRevenue.setText(revenueText); // Tổng doanh thu
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Fine>>> call, Throwable t) {
                tvYearlyRevenue.setText("Lỗi kết nối");
            }
        });
    }

    // TopBook model class (Giữ nguyên để tương thích với TopBookAdapter cũ của bạn)
    public static class TopBook {
        private String title;
        private String author;
        private int quantitySold;

        public TopBook(String title, String author, int quantitySold) {
            this.title = title;
            this.author = author;
            this.quantitySold = quantitySold;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public int getQuantitySold() { return quantitySold; }
    }
}