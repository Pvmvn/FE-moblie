package com.example.du_an_androidd;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.du_an_androidd.api.ApiClient;
import com.example.du_an_androidd.api.ApiService;
import com.example.du_an_androidd.model.ApiResponse;
import com.example.du_an_androidd.model.request.BookRequest;
import com.example.du_an_androidd.model.response.Book;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "book";
    public static final String EXTRA_BOOK_ID = "book_id";

    // Khai báo biến toàn cục để dùng lại khi reload
    private Book currentBook;
    private int bookId = -1;

    private ImageView imgBookCover;
    private TextView tvBookTitle, tvAuthor, tvYear, tvPages, tvRating;
    private TextView tvStockStatus, tvDescription, tvIsbn, tvPublisher, tvLanguage;
    private ProgressBar progressBarStock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // 1. Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 2. Ánh xạ View
        imgBookCover = findViewById(R.id.imgBookCover);
        tvBookTitle = findViewById(R.id.tvBookTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvYear = findViewById(R.id.tvYear);
        tvPages = findViewById(R.id.tvPages);
        tvRating = findViewById(R.id.tvRating);
        tvStockStatus = findViewById(R.id.tvStockStatus);
        progressBarStock = findViewById(R.id.progressBarStock);
        tvDescription = findViewById(R.id.tvDescription);
        tvIsbn = findViewById(R.id.tvIsbn);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvLanguage = findViewById(R.id.tvLanguage);
        Button btnEdit = findViewById(R.id.btnEdit);

        // 3. Nhận dữ liệu ban đầu
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_BOOK)) {
            currentBook = (Book) intent.getSerializableExtra(EXTRA_BOOK);
            if (currentBook != null) {
                bookId = currentBook.getId();
                displayBookDetails(currentBook);
            }
        }

        // 4. Xử lý nút Sửa -> Gọi hàm mở Dialog
        btnEdit.setOnClickListener(v -> {
            if (currentBook != null) {
                showEditBookDialog(currentBook);
            } else {
                Toast.makeText(this, "Chưa tải xong dữ liệu sách", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm hiển thị dữ liệu lên màn hình
    private void displayBookDetails(Book book) {
        if (book == null) return;

        tvBookTitle.setText(book.getTitle());

        // Tác giả
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            tvAuthor.setText(book.getAuthors().get(0).getName());
        } else {
            tvAuthor.setText("Chưa cập nhật");
        }

        // Năm, Trang, Đánh giá
        tvYear.setText(String.valueOf(book.getYear()));
        tvPages.setText("300"); // Fake
        tvRating.setText("4.5");

        // Kho
        tvStockStatus.setText("Còn: " + book.getAvailableCopies());

        // Mô tả & Chi tiết
        tvDescription.setText(book.getDescription() != null ? book.getDescription() : "Không có mô tả");
        tvPublisher.setText(book.getPublisher() != null ? book.getPublisher() : "---");
        tvIsbn.setText(book.getIsbn() != null ? book.getIsbn() : "---");
        tvLanguage.setText("Tiếng Việt");

        // Ảnh
        String imageUrl = book.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Nếu URL là relative path (bắt đầu bằng /), thêm BASE_URL
            if (imageUrl.startsWith("/")) {
                imageUrl = "http://10.0.2.2:3000" + imageUrl;
            }
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_book)
                    .error(R.drawable.ic_book)
                    .centerCrop()
                    .into(imgBookCover);
        } else {
            imgBookCover.setImageResource(R.drawable.ic_book);
        }
    }

    // Hàm mở Dialog Sửa sách (Tái sử dụng layout dialog_add_book)
    private void showEditBookDialog(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Dùng lại layout thêm sách vì các trường giống hệt nhau
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_book, null);
        builder.setView(dialogView);

        // Ánh xạ View trong Dialog
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etAuthor = dialogView.findViewById(R.id.etAuthor);
        EditText etPublisher = dialogView.findViewById(R.id.etPublisher);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);
        EditText etCategoryId = dialogView.findViewById(R.id.etCategoryId);
        EditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Đổi tên nút Lưu -> Cập nhật cho hợp ngữ cảnh
        btnSave.setText("Cập nhật");

        // --- ĐIỀN DỮ LIỆU CŨ VÀO Ô NHẬP ---
        etTitle.setText(book.getTitle());
        etPublisher.setText(book.getPublisher());
        etImageUrl.setText(book.getImageUrl());
        etYear.setText(String.valueOf(book.getYear()));
        etQuantity.setText(String.valueOf(book.getQuantity()));

        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            etAuthor.setText(String.valueOf(book.getAuthors().get(0).getId()));
        }

        AlertDialog dialog = builder.create();

        // Xử lý nút Lưu
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                Toast.makeText(this, "Tên sách không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo Request Update
            BookRequest request = new BookRequest();
            request.setTitle(title);
            request.setPublisher(etPublisher.getText().toString());
            request.setImageUrl(etImageUrl.getText().toString());
            request.setIsbn(book.getIsbn()); // Giữ nguyên ISBN cũ
            request.setDescription(book.getDescription()); // Giữ nguyên mô tả cũ

            try { request.setYear(Integer.parseInt(etYear.getText().toString())); } catch (Exception e) { request.setYear(2024); }
            try { request.setQuantity(Integer.parseInt(etQuantity.getText().toString())); } catch (Exception e) { request.setQuantity(1); }
            
            // Mặc định categoryId = 1 (đã ẩn trường nhập liệu)
            request.setCategoryId(1);

            // Mặc định authorIds = [1] (đã ẩn trường nhập liệu)
            List<Integer> authIds = new ArrayList<>();
            authIds.add(1);
            request.setAuthorIds(authIds);

            // Gọi API Update
            ApiService apiService = ApiClient.getService(this);
            apiService.updateBook(book.getId(), request).enqueue(new Callback<ApiResponse<Book>>() {
                @Override
                public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(BookDetailActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                        // Cập nhật lại dữ liệu trên màn hình ngay lập tức
                        currentBook = response.body().getData();
                        displayBookDetails(currentBook);

                        dialog.dismiss();
                    } else {
                        Toast.makeText(BookDetailActivity.this, "Lỗi cập nhật: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
                    Toast.makeText(BookDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}