package com.example.du_an_androidd;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log; // Import Log để soi lỗi
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
                Toast.makeText(getContext(), "Chức năng xóa đang phát triển", Toast.LENGTH_SHORT).show();
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

        // Ánh xạ
        com.google.android.material.textfield.TextInputEditText etTitle = dialogView.findViewById(R.id.etTitle);
        com.google.android.material.textfield.TextInputEditText etAuthor = dialogView.findViewById(R.id.etAuthor); // Nhập ID Tác giả
        com.google.android.material.textfield.TextInputEditText etPublisher = dialogView.findViewById(R.id.etPublisher);
        com.google.android.material.textfield.TextInputEditText etCategoryId = dialogView.findViewById(R.id.etCategoryId); // Nhập ID Thể loại

        // Ẩn các trường không dùng khi tạo sách (Price, Quantity)
        View etPriceLayout = dialogView.findViewById(R.id.etPrice).getParent() instanceof View ? (View) dialogView.findViewById(R.id.etPrice).getParent() : dialogView.findViewById(R.id.etPrice);
        etPriceLayout.setVisibility(View.GONE);

        View etQuantityLayout = dialogView.findViewById(R.id.etQuantity).getParent() instanceof View ? (View) dialogView.findViewById(R.id.etQuantity).getParent() : dialogView.findViewById(R.id.etQuantity);
        etQuantityLayout.setVisibility(View.GONE);

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        AlertDialog dialog = builder.create();

        // Điền dữ liệu cũ (nếu sửa)
        if (book != null) {
            etTitle.setText(book.getTitle());
            etPublisher.setText(book.getPublisher());
            // etCategoryId.setText(...)
        }

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String publisher = etPublisher.getText().toString().trim();
            String categoryIdStr = etCategoryId.getText().toString().trim();
            String authorIdStr = etAuthor.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập tên sách", Toast.LENGTH_SHORT).show();
                return;
            }

            if (book == null) {
                // --- XỬ LÝ DỮ LIỆU AN TOÀN ---

                // 1. Category ID (Mặc định 1 nếu nhập sai)
                int catId = 1;
                try { catId = Integer.parseInt(categoryIdStr); } catch (Exception e) {}

                // 2. Author IDs (Lấy từ ô nhập, mặc định 1)
                List<Integer> authorIds = new ArrayList<>();
                try {
                    int aId = Integer.parseInt(authorIdStr);
                    authorIds.add(aId);
                } catch (Exception e) {
                    authorIds.add(1); // Mặc định ID 1
                }

                // 3. Tạo ISBN Ngẫu nhiên (QUAN TRỌNG: Để không bị trùng)
                String randomIsbn = "ISBN-" + System.currentTimeMillis();

                // 4. Tạo Request
                BookRequest request = new BookRequest(title, randomIsbn, publisher, 2024, catId, authorIds);

                // 5. GỌI API & LOG LỖI CHI TIẾT
                ApiClient.getService(getContext()).addBook(request).enqueue(new Callback<ApiResponse<Book>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Book>> call, Response<ApiResponse<Book>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(getContext(), "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                            loadBooksFromApi();
                            dialog.dismiss();
                        } else {
                            // --- ĐOẠN NÀY ĐỂ SOI LỖI ---
                            String errorMsg = "Mã lỗi: " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg += " - Chi tiết: " + response.errorBody().string();
                                }
                            } catch (Exception e) {}

                            Log.e("API_ADD_BOOK", errorMsg); // Xem trong Logcat
                            Toast.makeText(getContext(), "Thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Book>> call, Throwable t) {
                        Log.e("API_ADD_BOOK", "Lỗi mạng: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                Toast.makeText(getContext(), "Tính năng sửa đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}