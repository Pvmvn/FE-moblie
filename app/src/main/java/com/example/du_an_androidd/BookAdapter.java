package com.example.du_an_androidd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// --- IMPORT MODEL MỚI ---
import com.example.du_an_androidd.model.response.Book;
import com.example.du_an_androidd.model.response.Author;
// -----------------------

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for Book list (Đã kết nối API Model)
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    // Sử dụng Model Book từ API response
    private List<Book> bookList;
    private OnBookClickListener listener;

    // Interface cập nhật để truyền object Book thật
    public interface OnBookClickListener {
        void onEditClick(Book book);
        void onDeleteClick(Book book);
        void onViewMoreClick(Book book);
    }

    // Constructor
    public BookAdapter(OnBookClickListener listener) {
        this.bookList = new ArrayList<>(); // Khởi tạo list rỗng tránh lỗi null
        this.listener = listener;
    }

    // Hàm cập nhật dữ liệu từ API
    public void setData(List<Book> list) {
        this.bookList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        if (book == null) return;

        // 1. Hiển thị Tiêu đề
        holder.tvBookTitle.setText(book.getTitle());

        // 2. Hiển thị Tác giả (Xử lý List<Author> từ API)
        String authorText = "Chưa cập nhật";

        // Kiểm tra nếu có danh sách tác giả và không rỗng
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            // Lấy tên tác giả đầu tiên
            authorText = book.getAuthors().get(0).getName();

            // Nếu có nhiều hơn 1 tác giả, thêm "..."
            if (book.getAuthors().size() > 1) {
                authorText += " và nnk";
            }
        } else if (book.getPublisher() != null) {
            // Nếu không có tác giả, hiển thị tạm Nhà xuất bản
            authorText = book.getPublisher();
        }

        holder.tvAuthor.setText("Tác giả: " + authorText);

        // 3. Hiển thị số lượng (Tùy chọn, nếu bạn muốn hiển thị thêm)
        // String qty = "SL: " + book.getAvailableCopies();

        // Sự kiện click
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(book));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(book));
        holder.btnViewMore.setOnClickListener(v -> listener.onViewMoreClick(book));
    }

    @Override
    public int getItemCount() {
        if (bookList != null) {
            return bookList.size();
        }
        return 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle;
        TextView tvAuthor;
        ImageButton btnEdit;
        ImageButton btnDelete;
        android.widget.Button btnViewMore;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnViewMore = itemView.findViewById(R.id.btnViewMore);
        }
    }
}