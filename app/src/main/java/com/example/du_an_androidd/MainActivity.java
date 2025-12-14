package com.example.du_an_androidd;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// --- IMPORT MỚI ---
import com.example.du_an_androidd.utils.TokenManager;
// -----------------

/**
 * Main Activity with Bottom Navigation
 * Chứa các fragments: Books, Invoices, Statistics, Account Management
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- BƯỚC BẢO MẬT (QUAN TRỌNG) ---
        // Kiểm tra xem User có Token đăng nhập chưa
        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null) {
            // Nếu chưa có Token -> Chuyển ngay về màn hình Đăng nhập
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish(); // Đóng MainActivity lại
            return; // Dừng chạy code bên dưới
        }
        // ---------------------------------

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment (Màn hình mặc định khi mở app là Quản lý sách)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new BookManagementFragment())
                    .commit();
        }

        // Xử lý sự kiện bấm menu dưới đáy
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_books) {
                selectedFragment = new BookManagementFragment();
            } else if (itemId == R.id.nav_invoices) {
                // Đổi tên biến cho dễ hiểu: InvoiceFragment thực chất là quản lý Mượn/Trả (Loans)
                selectedFragment = new InvoiceManagementFragment();
            } else if (itemId == R.id.nav_statistics) {
                selectedFragment = new StatisticsFragment();
            } else if (itemId == R.id.nav_account) {
                selectedFragment = new AccountManagementFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }
}