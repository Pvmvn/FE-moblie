package com.example.du_an_androidd;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.du_an_androidd.model.response.Loan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoanHistoryAdapter extends RecyclerView.Adapter<LoanHistoryAdapter.LoanHistoryViewHolder> {

    private List<Loan> loanList;

    public LoanHistoryAdapter() {
        this.loanList = new ArrayList<>();
    }

    public void setData(List<Loan> list) {
        this.loanList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LoanHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loan_history, parent, false);
        return new LoanHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanHistoryViewHolder holder, int position) {
        Loan loan = loanList.get(position);
        if (loan == null) return;

        // Set member name
        holder.tvMemberName.setText(loan.getMemberName() != null ? loan.getMemberName() : "Không xác định");

        // Set status
        String status = loan.getStatus();
        if (status != null) {
            if (status.equalsIgnoreCase("borrowed") || status.equalsIgnoreCase("đang mượn")) {
                holder.tvLoanStatus.setText("Đang mượn");
                holder.tvLoanStatus.setTextColor(Color.parseColor("#10B981"));
            } else if (status.equalsIgnoreCase("returned") || status.equalsIgnoreCase("đã trả")) {
                holder.tvLoanStatus.setText("Đã trả");
                holder.tvLoanStatus.setTextColor(Color.parseColor("#10B981"));
            } else {
                holder.tvLoanStatus.setText(status);
                holder.tvLoanStatus.setTextColor(Color.parseColor("#10B981"));
            }
        }

        // Set due date or return date
        String dueDate = loan.getDueAt();
        String returnedDate = loan.getReturnedAt();
        
        if (returnedDate != null && !returnedDate.isEmpty()) {
            // Calculate if returned late
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date due = dueDate != null ? sdf.parse(dueDate) : null;
                Date returned = sdf.parse(returnedDate.substring(0, Math.min(10, returnedDate.length())));
                
                if (due != null && returned.after(due)) {
                    long diff = (returned.getTime() - due.getTime()) / (1000 * 60 * 60 * 24);
                    holder.tvDueDate.setText("Trễ " + diff + " ngày");
                    holder.tvDueDate.setTextColor(Color.parseColor("#EF4444"));
                } else {
                    holder.tvDueDate.setText("Đúng hạn");
                    holder.tvDueDate.setTextColor(Color.parseColor("#94A3B8"));
                }
            } catch (Exception e) {
                holder.tvDueDate.setText("Đã trả");
            }
        } else if (dueDate != null && !dueDate.isEmpty()) {
            // Format date
            try {
                SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat sdfOutput = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdfInput.parse(dueDate.substring(0, Math.min(10, dueDate.length())));
                holder.tvDueDate.setText("Hạn trả: " + sdfOutput.format(date));
            } catch (Exception e) {
                holder.tvDueDate.setText("Hạn trả: " + dueDate.substring(0, Math.min(10, dueDate.length())));
            }
        }

        // Set time ago
        String borrowedAt = loan.getBorrowedAt();
        if (borrowedAt != null && !borrowedAt.isEmpty()) {
            holder.tvTimeAgo.setText(formatTimeAgo(borrowedAt));
        } else {
            holder.tvTimeAgo.setText("");
        }

        // Set default avatar
        holder.imgMemberAvatar.setImageResource(R.drawable.ic_person);
    }

    private String formatTimeAgo(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(dateString);
            if (date == null) {
                return "";
            }

            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                if (days == 1) {
                    return "Hôm qua";
                } else if (days < 7) {
                    return days + " ngày trước";
                } else {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    return outputFormat.format(date);
                }
            } else if (hours > 0) {
                return hours + " giờ trước";
            } else if (minutes > 0) {
                return minutes + " phút trước";
            } else {
                return "Vừa xong";
            }
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return loanList != null ? loanList.size() : 0;
    }

    static class LoanHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMemberAvatar;
        TextView tvMemberName;
        TextView tvLoanStatus;
        TextView tvDueDate;
        TextView tvTimeAgo;

        public LoanHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMemberAvatar = itemView.findViewById(R.id.imgMemberAvatar);
            tvMemberName = itemView.findViewById(R.id.tvMemberName);
            tvLoanStatus = itemView.findViewById(R.id.tvLoanStatus);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
        }
    }
}

