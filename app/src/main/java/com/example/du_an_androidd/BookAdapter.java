package com.example.du_an_androidd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView; // Mới: Import ImageView
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// --- IMPORT MODEL MỚI ---
import com.example.du_an_androidd.model.response.Book;
import com.example.du_an_androidd.model.response.Author;
// -----------------------

// --- IMPORT GLIDE ---
import com.bumptech.glide.Glide;
// --------------------

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView Adapter for Book list (Đã cập nhật hiển thị Ảnh và Số lượng)
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> bookList;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onEditClick(Book book);
        void onDeleteClick(Book book);
        void onViewMoreClick(Book book);
    }

    public BookAdapter(OnBookClickListener listener) {
        this.bookList = new ArrayList<>();
        this.listener = listener;
    }

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

        // 2. Hiển thị Tác giả
        String authorText = "Chưa cập nhật";
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            authorText = book.getAuthors().get(0).getName();
            if (book.getAuthors().size() > 1) {
                authorText += " và nnk";
            }
        } else if (book.getPublisher() != null) {
            authorText = book.getPublisher();
        }
        holder.tvAuthor.setText("Tác giả: " + authorText);

        // 3. [MỚI] Hiển thị Số Lượng
        // Dùng getQuantity() từ Model mới cập nhật
        holder.tvQuantity.setText("SL: " + book.getQuantity());

        // 4. [MỚI] Load Ảnh Bìa bằng Glide
        String imageUrl = book.getImageUrl();

        // Kiểm tra xem link ảnh có hợp lệ không
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl) // Link ảnh từ API
                    .placeholder(R.drawable.ic_book) // Ảnh hiển thị trong lúc chờ tải
                    .error(R.drawable.ic_book)       // Ảnh hiển thị nếu link lỗi
                    .centerCrop()                    // Cắt ảnh cho vừa khung
                    .into(holder.imgBookCover);      // Đẩy vào ImageView
        } else {
            // Nếu không có link ảnh, set ảnh mặc định
            holder.imgBookCover.setImageResource(R.drawable.ic_book);
        }

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

        // [MỚI] Khai báo thêm view hiển thị ảnh và số lượng
        ImageView imgBookCover;
        TextView tvQuantity;

        ImageButton btnEdit;
        ImageButton btnDelete;
        android.widget.Button btnViewMore;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);

            // [MỚI] Ánh xạ view mới từ item_book.xml
            imgBookCover = itemView.findViewById(R.id.imgBookCover);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnViewMore = itemView.findViewById(R.id.btnViewMore);
        }
    }
}