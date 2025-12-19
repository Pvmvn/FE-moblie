package com.example.du_an_androidd;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText; // Import cái này

// --- IMPORT API ---
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

public class BookManagementFragment extends Fragment {

    private RecyclerView rvBooks;
    private FloatingActionButton fabAddBook;
    private BookAdapter bookAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_management, container, false);

        rvBooks = view.findViewById(R.id.rvBooks);
        fabAddBook = view.findViewById(R.id.fabAddBook);

        // 1. Cấu hình Adapter
        bookAdapter = new BookAdapter(new BookAdapter.OnBookClickListener() {
            @Override
            public void onEditClick(Book book) {
                showAddBookDialog(book);
            }

            @Override
            public void onDeleteClick(Book book) {
                // Gọi hàm xóa sách
                deleteBookApi(book);
            }

            @Override
            public void onViewMoreClick(Book book) {
                Toast.makeText(getContext(), "Chi tiết: " + book.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBooks.setAdapter(bookAdapter);

        // 2. Tải dữ liệu
        loadBooksFromApi();

        fabAddBook.setOnClickListener(v -> showAddBookDialog(null));

        return view;
    }

    private void loadBooksFromApi() {
        ApiService apiService = ApiClient.getService(getContext());
        apiService.getBooks(1, 100).enqueue(new Callback<ApiResponse<List<Book>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Book>>> call, Response<ApiResponse<List<Book>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    bookAdapter.setData(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Book>>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddBookDialog(Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_book, null);
        builder.setView(dialogView);

        // --- 1. ÁNH XẠ CÁC TRƯỜNG MỚI (Khớp với dialog_add_book.xml mới) ---
        TextInputEditText etTitle = dialogView.findViewById(R.id.etTitle);
        TextInputEditText etImageUrl = dialogView.findViewById(R.id.etImageUrl); // Mới
        TextInputEditText etPublisher = dialogView.findViewById(R.id.etPublisher);
        TextInputEditText etYear = dialogView.findViewById(R.id.etYear);         // Mới
        TextInputEditText etAuthor = dialogView.findViewById(R.id.etAuthor);
        TextInputEditText etCategoryId = dialogView.findViewById(R.id.etCategoryId);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.etQuantity);   // Mới (đã hiện)

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        AlertDialog dialog = builder.create();

        // --- 2. ĐIỀN DỮ LIỆU CŨ (Nếu là chức năng Sửa) ---
        if (book != null) {
            etTitle.setText(book.getTitle());
            etPublisher.setText(book.getPublisher());
            etImageUrl.setText(book.getImageUrl());
            etYear.setText(String.valueOf(book.getYear()));
            etQuantity.setText(String.valueOf(book.getQuantity()));

            // Xử lý hiển thị ID tác giả (lấy ID đầu tiên nếu có)
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                etAuthor.setText(String.valueOf(book.getAuthors().get(0).getId()));
            }

            // Note: Category ID chưa có trong response Book cũ, tạm thời để trống hoặc mặc định
        }

        // --- 3. XỬ LÝ SỰ KIỆN LƯU ---
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            String publisher = etPublisher.getText().toString().trim();
            String yearStr = etYear.getText().toString().trim();
            String categoryIdStr = etCategoryId.getText().toString().trim();
            String authorIdStr = etAuthor.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();

            // Validate cơ bản
            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên sách", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse số liệu (Xử lý lỗi nếu người dùng nhập chữ)
            int year = 2024;
            try { year = Integer.parseInt(yearStr); } catch (Exception e) {}

            int quantity = 0;
            try { quantity = Integer.parseInt(quantityStr); } catch (Exception e) {}

            int catId = 1; // Default category
            try { catId = Integer.parseInt(categoryIdStr); } catch (Exception e) {}

            List<Integer> authorIds = new ArrayList<>();
            try {
                authorIds.add(Integer.parseInt(authorIdStr));
            } catch (Exception e) {
                authorIds.add(1); // Default author ID 1 nếu không nhập
            }

            // Tạo ISBN ngẫu nhiên (hoặc nhập nếu muốn)
            String randomIsbn = "ISBN-" + System.currentTimeMillis();

            // Tạo Request với đầy đủ thông tin mới
            BookRequest request = new BookRequest(
                    title, randomIsbn, publisher, year, catId, authorIds, imageUrl, quantity
            );

            if (book == null) {
                // --- THÊM MỚI ---
                ApiClient.getService(getContext()).addBook(request).enqueue(new Callback<ApiResponse<Book>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(getContext(), "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                            loadBooksFromApi(); // Load lại danh sách
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // --- CẬP NHẬT (EDIT) ---
                ApiClient.getService(getContext()).updateBook(book.getId(), request).enqueue(new Callback<ApiResponse<Book>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            loadBooksFromApi();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Cập nhật lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Hàm xóa sách
    private void deleteBookApi(Book book) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn xóa sách: " + book.getTitle() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    ApiClient.getService(getContext()).deleteBook(book.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Đã xóa sách", Toast.LENGTH_SHORT).show();
                                loadBooksFromApi();
                            } else {
                                Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}