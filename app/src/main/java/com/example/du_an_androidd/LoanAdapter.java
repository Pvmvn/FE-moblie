package com.example.du_an_androidd;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.du_an_androidd.model.response.Loan;
import java.util.ArrayList;
import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.LoanViewHolder> {

    private List<Loan> loanList;
    private OnLoanClickListener listener;

    // Interface chỉ còn 2 hàm (Đã xóa onDetailClick)
    public interface OnLoanClickListener {
        void onRenewClick(Loan loan);
        void onReturnClick(Loan loan);
    }

    public LoanAdapter(OnLoanClickListener listener) {
        this.loanList = new ArrayList<>();
        this.listener = listener;
    }

    public void setData(List<Loan> list) {
        this.loanList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LoanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loan, parent, false);
        return new LoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanViewHolder holder, int position) {
        Loan loan = loanList.get(position);
        if (loan == null) return;

        holder.tvBookTitle.setText(loan.getBookTitle());
        holder.tvMemberInfo.setText(loan.getMemberName());

        // Load ảnh
        if (loan.getBookImageUrl() != null) {
            Glide.with(holder.itemView.getContext()).load(loan.getBookImageUrl()).placeholder(R.drawable.ic_book).into(holder.imgBookCover);
        }

        // Xử lý nút: Nếu đã trả rồi thì ẩn nút đi
        boolean isReturned = loan.getReturnedAt() != null && !loan.getReturnedAt().isEmpty();
        if (isReturned) {
            holder.btnRenew.setVisibility(View.GONE);
            holder.btnReturn.setVisibility(View.GONE);
            holder.tvStatusTag.setText("Đã trả");
            holder.tvStatusTag.setTextColor(Color.GRAY);
        } else {
            holder.btnRenew.setVisibility(View.VISIBLE);
            holder.btnReturn.setVisibility(View.VISIBLE);
            holder.tvStatusTag.setText("Đang mượn");
            holder.tvStatusTag.setTextColor(Color.parseColor("#10B981")); // Xanh
        }

        holder.btnRenew.setOnClickListener(v -> listener.onRenewClick(loan));
        holder.btnReturn.setOnClickListener(v -> listener.onReturnClick(loan));
    }

    @Override
    public int getItemCount() { return loanList != null ? loanList.size() : 0; }

    static class LoanViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBookCover;
        TextView tvBookTitle, tvMemberInfo, tvStatusTag, tvDueDate, tvAuthor;
        Button btnRenew, btnReturn;

        public LoanViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvMemberInfo = itemView.findViewById(R.id.tvMemberInfo);
            tvStatusTag = itemView.findViewById(R.id.tvStatusTag);
            // tvDueDate = itemView.findViewById(R.id.tvDueDate); // Nếu có lỗi thì comment dòng này
            btnRenew = itemView.findViewById(R.id.btnRenew);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            // Đã xóa btnDetail
        }
    }
}