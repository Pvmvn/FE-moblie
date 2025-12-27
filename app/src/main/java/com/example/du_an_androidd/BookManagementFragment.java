package com.example.du_an_androidd;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText; // QUAN TRỌNG: Dùng EditText thường
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        setupRecyclerView();
        loadBooksFromApi();

        fabAddBook.setOnClickListener(v -> showAddBookDialog(null));

        return view;
    }

    private void setupRecyclerView() {
        bookAdapter = new BookAdapter(new BookAdapter.OnBookClickListener() {
            @Override
            public void onEditClick(Book book) {
                showAddBookDialog(book);
            }

            @Override
            public void onDeleteClick(Book book) {
                deleteBookApi(book);
            }

            @Override
            public void onViewMoreClick(Book book) {
                Intent intent = new Intent(getContext(), BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_BOOK, book);
                startActivity(intent);
            }
        });

        rvBooks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBooks.setAdapter(bookAdapter);
    }

    private void loadBooksFromApi() {
        ApiService apiService = ApiClient.getService(getContext());
        apiService.getBooks(1, 100).enqueue(new Callback<ApiResponse<List<Book>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Book>>> call, Response<ApiResponse<List<Book>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    bookAdapter.setData(response.body().getData());
                } else {
                    Toast.makeText(getContext(), "Không tải được danh sách", Toast.LENGTH_SHORT).show();
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

        // --- SỬA LỖI: Dùng EditText thay vì TextInputEditText ---
        EditText etTitle = dialogView.findViewById(R.id.etTitle);
        EditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);
        EditText etPublisher = dialogView.findViewById(R.id.etPublisher);
        EditText etYear = dialogView.findViewById(R.id.etYear);
        EditText etAuthor = dialogView.findViewById(R.id.etAuthor);
        EditText etCategoryId = dialogView.findViewById(R.id.etCategoryId);
        EditText etQuantity = dialogView.findViewById(R.id.etQuantity);

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        AlertDialog dialog = builder.create();

        // Điền dữ liệu cũ nếu là chế độ Sửa
        if (book != null) {
            etTitle.setText(book.getTitle());
            etPublisher.setText(book.getPublisher());
            etImageUrl.setText(book.getImageUrl());
            etYear.setText(String.valueOf(book.getYear()));
            etQuantity.setText(String.valueOf(book.getQuantity()));
            // Lấy ID tác giả đầu tiên (nếu có)
            if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                etAuthor.setText(String.valueOf(book.getAuthors().get(0).getId()));
            }
            // Category ID chưa có trong response mẫu, để trống hoặc mặc định
        }

        btnSave.setOnClickListener(v -> {
            // Lấy dữ liệu từ ô nhập
            String title = etTitle.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();
            String publisher = etPublisher.getText().toString().trim();
            String yearStr = etYear.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            String authorIdStr = etAuthor.getText().toString().trim();
            String categoryIdStr = etCategoryId.getText().toString().trim();

            // Validate cơ bản
            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên sách", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse số (Xử lý lỗi nếu người dùng nhập chữ hoặc để trống)
            int year = 2024;
            try { year = Integer.parseInt(yearStr); } catch (Exception e) {}

            int quantity = 1;
            try { quantity = Integer.parseInt(quantityStr); } catch (Exception e) {}

            int categoryId = 1;
            try { categoryId = Integer.parseInt(categoryIdStr); } catch (Exception e) {}

            List<Integer> authorIds = new ArrayList<>();
            try {
                if(!authorIdStr.isEmpty()) authorIds.add(Integer.parseInt(authorIdStr));
                else authorIds.add(1); // Mặc định ID 1 nếu trống
            } catch (Exception e) { authorIds.add(1); }

            // Tạo Request
            BookRequest request = new BookRequest();
            request.setTitle(title);
            request.setIsbn("ISBN-" + System.currentTimeMillis()); // Fake ISBN ngẫu nhiên
            request.setPublisher(publisher);
            request.setYear(year);
            request.setCategoryId(categoryId);
            request.setAuthorIds(authorIds);
            request.setImageUrl(imageUrl);
            request.setQuantity(quantity);
            request.setDescription("Mô tả sách..."); // Có thể thêm ô nhập mô tả sau

            ApiService service = ApiClient.getService(getContext());

            // Callback chung cho cả Thêm và Sửa
            Callback<ApiResponse<Book>> commonCallback = new Callback<ApiResponse<Book>>() {
                @Override
                public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), (book == null) ? "Thêm thành công!" : "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        loadBooksFromApi(); // Load lại danh sách
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
                    Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

            if (book == null) {
                // THÊM MỚI
                service.addBook(request).enqueue(commonCallback);
            } else {
                // CẬP NHẬT
                service.updateBook(book.getId(), request).enqueue(commonCallback);
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void deleteBookApi(Book book) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sách: " + book.getTitle() + "?")
                .setPositiveButton("Xóa", (d, w) -> {
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