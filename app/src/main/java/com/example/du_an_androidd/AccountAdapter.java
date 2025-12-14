package com.example.du_an_androidd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Import Model mới
import com.example.du_an_androidd.model.response.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter hiển thị danh sách Độc giả (Members) từ API
 */
public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    // Thay đổi từ List<Account> cũ sang List<Member> mới
    private List<Member> memberList;

    // Constructor: Khởi tạo list rỗng để tránh lỗi null
    public AccountAdapter() {
        this.memberList = new ArrayList<>();
    }

    // Hàm cập nhật dữ liệu mới từ API vào Adapter
    public void setData(List<Member> list) {
        this.memberList = list;
        notifyDataSetChanged(); // Làm mới giao diện
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Giữ nguyên layout item_account
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Member member = memberList.get(position);

        if (member == null) return;

        // Map dữ liệu từ API vào giao diện
        // Ví dụ: Hiển thị Tên ở dòng trên
        holder.tvUsername.setText(member.getFullName());

        // Ví dụ: Hiển thị Mã thẻ và SĐT ở dòng dưới
        String info = "Mã: " + member.getMemberCode();

        // Kiểm tra null để tránh lỗi crash nếu dữ liệu thiếu
        if (member.getPhone() != null) {
            info += " | SĐT: " + member.getPhone();
        }

        holder.tvInformation.setText(info);
    }

    @Override
    public int getItemCount() {
        if (memberList != null) {
            return memberList.size();
        }
        return 0;
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvInformation;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ file layout xml (giữ nguyên ID cũ của bạn)
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvInformation = itemView.findViewById(R.id.tvInformation);
        }
    }
}