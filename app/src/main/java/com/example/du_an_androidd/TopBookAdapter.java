package com.example.du_an_androidd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for Top Books list
 */
public class TopBookAdapter extends RecyclerView.Adapter<TopBookAdapter.TopBookViewHolder> {

    // Sử dụng Class nội bộ TopBook trong StatisticsFragment
    private List<StatisticsFragment.TopBook> topBookList;

    public TopBookAdapter(List<StatisticsFragment.TopBook> topBookList) {
        // Khởi tạo list nếu null để tránh crash
        if (topBookList == null) {
            this.topBookList = new ArrayList<>();
        } else {
            this.topBookList = topBookList;
        }
    }

    // --- THÊM HÀM NÀY ĐỂ CẬP NHẬT DỮ LIỆU TỪ API ---
    public void setData(List<StatisticsFragment.TopBook> list) {
        this.topBookList = list;
        notifyDataSetChanged(); // Làm mới giao diện
    }
    // -----------------------------------------------

    @NonNull
    @Override
    public TopBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_book, parent, false);
        return new TopBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopBookViewHolder holder, int position) {
        StatisticsFragment.TopBook book = topBookList.get(position);

        // Kiểm tra null an toàn
        if (book == null) return;

        holder.tvBookTitle.setText("Tiêu Đề: " + book.getTitle());
        holder.tvAuthor.setText("Tác Giả: " + book.getAuthor());

        // Trong StatisticsFragment mình dùng available_copies làm số lượng bán giả lập
        // Nên ở đây hiển thị là "Số lượng" hoặc "Tồn kho" tùy bạn
        holder.tvQuantitySold.setText("Số Lượng: " + book.getQuantitySold());
    }

    @Override
    public int getItemCount() {
        if (topBookList != null) {
            return topBookList.size();
        }
        return 0;
    }

    static class TopBookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle;
        TextView tvAuthor;
        TextView tvQuantitySold;

        public TopBookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvQuantitySold = itemView.findViewById(R.id.tvQuantitySold);
        }
    }
}