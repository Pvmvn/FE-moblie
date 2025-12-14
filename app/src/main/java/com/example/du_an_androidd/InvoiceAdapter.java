package com.example.du_an_androidd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// --- IMPORT MODEL MỚI ---
import com.example.du_an_androidd.model.response.Loan;
// -----------------------

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for Loan/Invoice list
 * Đã chuyển đổi để sử dụng dữ liệu Loan (Phiếu mượn) từ API
 */
public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    // Thay đổi từ Invoice giả sang Loan thật
    private List<Loan> loanList;
    private OnInvoiceClickListener listener;

    public interface OnInvoiceClickListener {
        void onViewDetailClick(Loan loan);
        void onDeleteClick(Loan loan); // Lưu ý: API thường là Trả sách (Return) chứ không phải xóa
    }

    public InvoiceAdapter(OnInvoiceClickListener listener) {
        this.loanList = new ArrayList<>();
        this.listener = listener;
    }

    // Hàm cập nhật dữ liệu từ API
    public void setData(List<Loan> list) {
        this.loanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Loan loan = loanList.get(position);
        if (loan == null) return;

        // Hiển thị thông tin từ API
        holder.tvInvoiceId.setText("Mã phiếu: " + loan.getId() + " - " + loan.getStatus());

        // Hiển thị ngày mượn. Nếu null thì để trống
        String date = loan.getBorrowedAt();
        if (date != null && date.length() > 10) {
            date = date.substring(0, 10); // Cắt chuỗi lấy ngày yyyy-mm-dd
        }
        holder.tvPurchaseDate.setText("Ngày mượn: " + date);

        // Bạn có thể hiển thị thêm tên sách hoặc tên người mượn nếu layout cho phép
        // Ví dụ: holder.tvInvoiceId.setText(loan.getBookTitle());

        holder.btnViewDetail.setOnClickListener(v -> listener.onViewDetailClick(loan));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(loan));
    }

    @Override
    public int getItemCount() {
        if (loanList != null) {
            return loanList.size();
        }
        return 0;
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvInvoiceId;
        TextView tvPurchaseDate;
        ImageButton btnViewDetail;
        ImageButton btnDelete;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInvoiceId = itemView.findViewById(R.id.tvInvoiceId);
            tvPurchaseDate = itemView.findViewById(R.id.tvPurchaseDate);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}