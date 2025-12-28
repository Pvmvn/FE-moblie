# 📱 TÀI LIỆU DỰ ÁN - ỨNG DỤNG QUẢN LÝ THƯ VIỆN (ANDROID)

## 📑 MỤC LỤC
1. [Tổng quan dự án](#tổng-quan-dự-án)
2. [Công nghệ sử dụng](#công-nghệ-sử-dụng)
3. [Cấu trúc thư mục](#cấu-trúc-thư-mục)
4. [Kiến trúc ứng dụng](#kiến-trúc-ứng-dụng)
5. [Giải thích code chi tiết](#giải-thích-code-chi-tiết)
6. [Luồng hoạt động chính](#luồng-hoạt-động-chính)
7. [API Integration](#api-integration)
8. [Xử lý dữ liệu](#xử-lý-dữ-liệu)

---

## 🎯 TỔNG QUAN DỰ ÁN

Ứng dụng **Quản lý Thư viện** là một ứng dụng Android native được xây dựng bằng **Java**, cho phép quản lý sách, mượn/trả sách, quản lý khách hàng và tài khoản. Ứng dụng kết nối với Backend API (Node.js/Express) để lấy và gửi dữ liệu.

### Chức năng chính:
- ✅ Đăng nhập / Đăng ký
- ✅ Quản lý sách (Thêm, Sửa, Xóa, Xem chi tiết)
- ✅ Quản lý mượn/trả sách
- ✅ Quản lý khách hàng
- ✅ Quản lý tài khoản
- ✅ Hiển thị danh sách sách với ảnh bìa

---

## 🛠 CÔNG NGHỆ SỬ DỤNG

### 1. **Ngôn ngữ & Framework**
- **Java** - Ngôn ngữ lập trình chính
- **Android SDK** - Framework phát triển ứng dụng Android
- **AndroidX Libraries** - Thư viện hỗ trợ của Android

### 2. **Thư viện chính**

#### **Retrofit 2.9.0**
```kotlin
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
```
- **Mục đích**: Thực hiện các HTTP requests (GET, POST, PUT, DELETE) tới Backend API
- **Ưu điểm**: 
  - Type-safe: Tự động convert JSON sang Java objects
  - Hỗ trợ async (không chặn UI thread)
  - Dễ dàng thêm interceptors (token, logging)

#### **Gson 2.10.1**
```kotlin
implementation("com.google.code.gson:gson:2.10.1")
```
- **Mục đích**: Convert JSON ↔ Java Objects
- **Ví dụ**: Server trả về JSON `{"id": 1, "title": "Sách hay"}` → Gson tự động tạo object `Book`

#### **Glide 4.16.0**
```kotlin
implementation("com.github.bumptech.glide:glide:4.16.0")
```
- **Mục đích**: Load và cache ảnh từ URL
- **Ưu điểm**:
  - Tự động cache ảnh (không cần load lại)
  - Placeholder & error image
  - Optimize memory
- **Ví dụ sử dụng**:
```java
Glide.with(context)
    .load(imageUrl)
    .placeholder(R.drawable.ic_book)  // Ảnh hiển thị khi đang load
    .error(R.drawable.ic_book)        // Ảnh hiển thị khi lỗi
    .into(imageView);
```

#### **Material Design Components**
```kotlin
implementation(libs.material)
```
- Bottom Navigation, Floating Action Button, Card Views
- Tạo giao diện đẹp, hiện đại theo Material Design

#### **RecyclerView**
- Hiển thị danh sách sách, khách hàng, mượn/trả
- Hiệu năng tốt hơn ListView (chỉ render items hiển thị)

---

## 📁 CẤU TRÚC THỰ MỤC

```
FE-moblie/app/src/main/
├── java/com/example/du_an_androidd/
│   ├── api/                          # Xử lý API
│   │   ├── ApiClient.java           # Cấu hình Retrofit & OkHttp
│   │   └── ApiService.java          # Định nghĩa các API endpoints
│   │
│   ├── model/                        # Data Models
│   │   ├── ApiResponse.java         # Wrapper cho response từ server
│   │   ├── request/                 # Models gửi lên server
│   │   │   ├── BookRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   └── ...
│   │   └── response/                # Models nhận từ server
│   │       ├── Book.java
│   │       ├── Author.java
│   │       ├── Loan.java
│   │       └── ...
│   │
│   ├── utils/                        # Utilities
│   │   └── TokenManager.java        # Quản lý JWT token (SharedPreferences)
│   │
│   ├── Activities/                   # Màn hình chính
│   │   ├── LoginActivity.java       # Màn đăng nhập
│   │   ├── MainActivity.java        # Màn chính (chứa Bottom Nav)
│   │   └── BookDetailActivity.java  # Chi tiết sách
│   │
│   └── Fragments/                    # Màn hình con (trong MainActivity)
│       ├── BookManagementFragment.java      # Quản lý sách
│       ├── InvoiceManagementFragment.java   # Quản lý mượn/trả
│       ├── CustomerManagementFragment.java  # Quản lý khách hàng
│       └── AccountManagementFragment.java   # Quản lý tài khoản
│
└── res/
    ├── layout/                       # XML layouts
    │   ├── activity_main.xml
    │   ├── activity_book_detail.xml
    │   ├── fragment_book_management.xml
    │   ├── dialog_add_book.xml
    │   └── item_book.xml
    │
    ├── drawable/                     # Icons, images
    ├── values/                       # Strings, colors, themes
    └── menu/                         # Menu definitions
```

---

## 🏗 KIẾN TRÚC ỨNG DỤNG

### 1. **MVC Pattern (Model-View-Controller)**

```
┌─────────────┐
│    VIEW     │  ← XML Layouts + Activities/Fragments
│  (Giao diện) │
└──────┬──────┘
       │ User Actions
       ▼
┌─────────────┐
│ CONTROLLER  │  ← Activities/Fragments (Xử lý logic)
│  (Logic)    │
└──────┬──────┘
       │ API Calls
       ▼
┌─────────────┐
│    MODEL    │  ← Book, Author, Loan... (Dữ liệu)
│   (Data)    │
└─────────────┘
```

### 2. **Luồng dữ liệu**

```
User Input → Fragment/Activity → ApiService → Backend API
                                    ↓
                            ApiResponse<Data>
                                    ↓
                            Update UI (RecyclerView)
```

---

## 📖 GIẢI THÍCH CODE CHI TIẾT

### 1. **ApiClient.java - Cấu hình Retrofit**

```java
public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    // ↑ 10.0.2.2 = localhost khi chạy trên Android Emulator
    
    public static ApiService getService(Context context) {
        // 1. Tạo OkHttpClient với Interceptor
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Timeout 30s
            .addInterceptor(chain -> {
                // 2. Thêm JWT Token vào Header
                String token = tokenManager.getToken();
                if (token != null) {
                    newRequest.addHeader("Authorization", "Bearer " + token);
                }
                return chain.proceed(newRequest.build());
            })
            .build();
        
        // 3. Tạo Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())  // Convert JSON ↔ Java
            .build();
        
        return retrofit.create(ApiService.class);
    }
}
```

**Giải thích**:
- **BASE_URL**: Địa chỉ server Backend
- **OkHttpClient**: HTTP client với các tính năng:
  - Timeout: Tránh app bị đơ khi mạng chậm
  - Interceptor: Tự động thêm Token vào mọi request
- **GsonConverterFactory**: Tự động convert JSON response thành Java objects

---

### 2. **ApiService.java - Định nghĩa API Endpoints**

```java
public interface ApiService {
    // GET danh sách sách
    @GET("books")
    Call<ApiResponse<List<Book>>> getBooks(
        @Query("page") int page, 
        @Query("limit") int limit
    );
    
    // POST thêm sách mới
    @POST("books")
    Call<ApiResponse<Book>> addBook(@Body BookRequest request);
    
    // PUT cập nhật sách
    @PUT("books/{id}")
    Call<ApiResponse<Book>> updateBook(
        @Path("id") int id, 
        @Body BookRequest request
    );
    
    // DELETE xóa sách
    @DELETE("books/{id}")
    Call<ApiResponse<Void>> deleteBook(@Path("id") int id);
}
```

**Giải thích**:
- **@GET, @POST, @PUT, @DELETE**: HTTP methods
- **@Query**: Query parameters (`?page=1&limit=10`)
- **@Path**: Path parameters (`/books/{id}` → `/books/5`)
- **@Body**: Request body (JSON)
- **Call<T>**: Async call, trả về Response sau khi hoàn thành

---

### 3. **BookManagementFragment.java - Quản lý sách**

#### **Load danh sách sách:**

```java
private void loadBooksFromApi() {
    ApiService apiService = ApiClient.getService(getContext());
    
    // Gọi API GET /books?page=1&limit=100
    apiService.getBooks(1, 100).enqueue(new Callback<ApiResponse<List<Book>>>() {
        @Override
        public void onResponse(Call<...> call, Response<...> response) {
            if (response.isSuccessful() && response.body() != null) {
                // ✅ Thành công: Cập nhật RecyclerView
                List<Book> books = response.body().getData();
                bookAdapter.setData(books);
            } else {
                // ❌ Lỗi từ server (401, 404, 500...)
                Toast.makeText(getContext(), "Không tải được danh sách", ...);
            }
        }
        
        @Override
        public void onFailure(Call<...> call, Throwable t) {
            // ❌ Lỗi mạng (không kết nối được server)
            Toast.makeText(getContext(), "Lỗi kết nối", ...);
        }
    });
}
```

**Giải thích**:
- **enqueue()**: Gọi API bất đồng bộ (không chặn UI thread)
- **onResponse()**: Callback khi server trả về (thành công hoặc lỗi HTTP)
- **onFailure()**: Callback khi lỗi mạng (timeout, không kết nối được)

#### **Thêm sách mới:**

```java
private void showAddBookDialog(Book book) {
    // 1. Tạo Dialog từ XML layout
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    View dialogView = LayoutInflater.from(getContext())
        .inflate(R.layout.dialog_add_book, null);
    builder.setView(dialogView);
    
    // 2. Ánh xạ các EditText
    EditText etTitle = dialogView.findViewById(R.id.etTitle);
    EditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);
    // ...
    
    // 3. Xử lý nút Lưu
    btnSave.setOnClickListener(v -> {
        // 4. Tạo BookRequest từ dữ liệu nhập
        BookRequest request = new BookRequest();
        request.setTitle(etTitle.getText().toString());
        request.setImageUrl(etImageUrl.getText().toString());
        request.setCategoryId(1);  // Mặc định = 1 (đã ẩn trường nhập)
        request.setAuthorIds(Arrays.asList(1));  // Mặc định = [1]
        
        // 5. Gọi API
        ApiService service = ApiClient.getService(getContext());
        if (book == null) {
            // THÊM MỚI
            service.addBook(request).enqueue(callback);
        } else {
            // CẬP NHẬT
            service.updateBook(book.getId(), request).enqueue(callback);
        }
    });
}
```

---

### 4. **BookAdapter.java - RecyclerView Adapter**

```java
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private List<Book> bookList;
    
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        
        // 1. Hiển thị text
        holder.tvBookTitle.setText(book.getTitle());
        
        // 2. Load ảnh bằng Glide
        String imageUrl = book.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_book)  // Ảnh khi đang load
                .error(R.drawable.ic_book)        // Ảnh khi lỗi
                .centerCrop()                     // Crop ảnh vừa khung
                .into(holder.imgBookCover);
        } else {
            // Nếu không có URL → dùng ảnh mặc định
            holder.imgBookCover.setImageResource(R.drawable.ic_book);
        }
        
        // 3. Xử lý click events
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(book));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(book));
        holder.btnViewMore.setOnClickListener(v -> listener.onViewMoreClick(book));
    }
}
```

**Giải thích**:
- **ViewHolder Pattern**: Giữ reference đến Views để tránh `findViewById()` nhiều lần
- **onBindViewHolder()**: Bind data vào View cho mỗi item
- **Glide**: Load ảnh async, tự động cache

---

### 5. **TokenManager.java - Quản lý JWT Token**

```java
public class TokenManager {
    private SharedPreferences prefs;
    private static final String KEY_TOKEN = "auth_token";
    
    public TokenManager(Context context) {
        prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }
    
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    
    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}
```

**Giải thích**:
- **SharedPreferences**: Lưu dữ liệu nhỏ (key-value) vào file XML
- **Lưu token**: Sau khi đăng nhập thành công
- **Đọc token**: Mỗi khi gọi API (trong ApiClient interceptor)
- **Xóa token**: Khi đăng xuất

---

### 6. **MainActivity.java - Activity chính**

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Kiểm tra Token (bảo mật)
        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null) {
            // Chưa đăng nhập → chuyển về LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);
        
        // 2. Setup Bottom Navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            
            if (item.getItemId() == R.id.nav_books) {
                selectedFragment = new BookManagementFragment();
            } else if (item.getItemId() == R.id.nav_loans) {
                selectedFragment = new InvoiceManagementFragment();
            }
            // ...
            
            // 3. Replace fragment trong container
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit();
            
            return true;
        });
    }
}
```

**Giải thích**:
- **Token check**: Bảo mật - chỉ cho phép truy cập nếu đã đăng nhập
- **Fragment Transaction**: Thay thế fragment khi user chọn tab
- **Bottom Navigation**: Menu điều hướng ở cuối màn hình

---

### 7. **BookDetailActivity.java - Chi tiết sách**

#### **Hiển thị ảnh sách:**

```java
private void displayBookDetails(Book book) {
    // ...
    
    // Xử lý ảnh
    String imageUrl = book.getImageUrl();
    if (imageUrl != null && !imageUrl.isEmpty()) {
        // Nếu URL là relative path (bắt đầu bằng /)
        // → Thêm BASE_URL phía trước
        if (imageUrl.startsWith("/")) {
            imageUrl = "http://10.0.2.2:3000" + imageUrl;
        }
        
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_book)  // Ảnh khi đang load
            .error(R.drawable.ic_book)        // Ảnh khi lỗi
            .centerCrop()                     // Crop ảnh
            .into(imgBookCover);
    } else {
        // Nếu không có URL → dùng ảnh mặc định
        imgBookCover.setImageResource(R.drawable.ic_book);
    }
}
```

**Giải thích**:
- **Relative URL**: Server có thể trả về `/uploads/images/book.jpg` → cần thêm BASE_URL
- **Glide error handler**: Hiển thị ảnh mặc định nếu load lỗi
- **centerCrop()**: Crop ảnh để vừa khung, không bị méo

---

## 🔄 LUỒNG HOẠT ĐỘNG CHÍNH

### 1. **Luồng Đăng nhập:**

```
LoginActivity
    ↓ User nhập username/password
    ↓ Click "Đăng nhập"
LoginRequest → ApiService.login()
    ↓
Backend API (/auth/login)
    ↓
Response: {token: "abc123", user: {...}}
    ↓
TokenManager.saveToken(token)
    ↓
Intent → MainActivity
```

### 2. **Luồng Xem danh sách sách:**

```
BookManagementFragment.onCreateView()
    ↓
loadBooksFromApi()
    ↓
ApiService.getBooks(1, 100)
    ↓
Backend API (/books?page=1&limit=100)
    ↓
Response: ApiResponse<List<Book>>
    ↓
bookAdapter.setData(books)
    ↓
RecyclerView hiển thị danh sách
```

### 3. **Luồng Thêm sách:**

```
User click FAB (Floating Action Button)
    ↓
showAddBookDialog(null)
    ↓
Dialog hiển thị form nhập liệu
    ↓
User nhập thông tin → Click "Lưu sách"
    ↓
Tạo BookRequest từ dữ liệu
    ↓
ApiService.addBook(request)
    ↓
Backend API (/books POST)
    ↓
Response: ApiResponse<Book>
    ↓
loadBooksFromApi() → Refresh danh sách
```

### 4. **Luồng Xem chi tiết sách:**

```
BookAdapter → User click "Chi tiết"
    ↓
Intent → BookDetailActivity
    ↓
Intent.putExtra("book", book)
    ↓
BookDetailActivity.onCreate()
    ↓
displayBookDetails(book)
    ↓
Hiển thị thông tin + ảnh bìa
```

---

## 🌐 API INTEGRATION

### Request/Response Format:

```java
// Request
BookRequest request = new BookRequest();
request.setTitle("Tên sách");
request.setImageUrl("https://example.com/image.jpg");
request.setCategoryId(1);
request.setAuthorIds(Arrays.asList(1));
request.setQuantity(100);

// Response từ Server
{
    "success": true,
    "message": "Tạo sách thành công",
    "data": {
        "id": 5,
        "title": "Tên sách",
        "image_url": "/uploads/images/book.jpg",
        ...
    }
}
```

### Error Handling:

```java
apiService.addBook(request).enqueue(new Callback<ApiResponse<Book>>() {
    @Override
    public void onResponse(Call<...> call, Response<...> response) {
        if (response.isSuccessful()) {
            // ✅ HTTP 200-299
            Book book = response.body().getData();
        } else {
            // ❌ HTTP 400, 401, 404, 500...
            int statusCode = response.code();
            // 401: Unauthorized → Chưa đăng nhập
            // 404: Not Found → Không tìm thấy
            // 500: Server Error → Lỗi server
        }
    }
    
    @Override
    public void onFailure(Call<...> call, Throwable t) {
        // ❌ Lỗi mạng (timeout, không kết nối được)
        // t.getMessage() → "Unable to resolve host"
    }
});
```

---

## 📊 XỬ LÝ DỮ LIỆU

### 1. **Model Classes (Gson Serialization):**

```java
public class Book implements Serializable {
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("image_url")  // JSON key → Java field
    private String imageUrl;
    
    @SerializedName("authors")
    private List<Author> authors;
    
    // Getters & Setters
    public String getImageUrl() { return imageUrl; }
}
```

**Giải thích**:
- **@SerializedName**: Map JSON key → Java field name
- **Serializable**: Cho phép truyền object qua Intent

### 2. **ApiResponse Wrapper:**

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;  // Generic type
    
    public boolean isSuccess() { return success; }
    public T getData() { return data; }
}
```

**Giải thích**:
- **Generic `<T>`**: Có thể dùng cho Book, List<Book>, Loan, ...
- **Wrapper pattern**: Server luôn trả về format `{success, message, data}`

---

## 🔧 CÁC THAY ĐỔI MỚI NHẤT

### 1. **Sửa lỗi hiển thị ảnh ở BookDetailActivity:**
- ✅ Thêm `.error()` handler cho Glide
- ✅ Xử lý relative URL (thêm BASE_URL nếu bắt đầu bằng `/`)
- ✅ Set ảnh mặc định nếu không có URL

### 2. **Ẩn trường ID Tác giả và ID Thể loại:**
- ✅ Ẩn các trường trong `dialog_add_book.xml` (`android:visibility="gone"`)
- ✅ Vẫn giữ EditText trong XML để code Java không lỗi
- ✅ Set giá trị mặc định (1) trong code Java

---

## 📝 GHI CHÚ QUAN TRỌNG

1. **BASE_URL**: `http://10.0.2.2:3000/` chỉ hoạt động trên Android Emulator. Khi test trên thiết bị thật, cần đổi thành IP máy tính (VD: `http://192.168.1.100:3000/`)

2. **Token Management**: Token được lưu trong SharedPreferences và tự động thêm vào mọi API request qua Interceptor

3. **Thread Safety**: Retrofit tự động chạy trên background thread, callback `onResponse()`/`onFailure()` chạy trên Main thread → có thể update UI trực tiếp

4. **Image Loading**: Glide tự động cache ảnh → không cần load lại mỗi lần mở app

5. **Fragment Lifecycle**: Fragment được tạo mới mỗi lần chọn tab → cần gọi API lại trong `onCreateView()`

---

## 🚀 HƯỚNG DẪN CHẠY DỰ ÁN

1. **Cài đặt Android Studio**
2. **Mở project**: File → Open → Chọn thư mục `FE-moblie`
3. **Chạy Backend**: Đảm bảo Backend API đang chạy ở `http://localhost:3000`
4. **Build & Run**: Click nút Run (▶️) hoặc `Shift + F10`
5. **Chọn Emulator**: Chọn máy ảo Android hoặc kết nối thiết bị thật

---

## 📚 TÀI LIỆU THAM KHẢO

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Glide Documentation](https://bumptech.github.io/glide/)
- [Android Developers Guide](https://developer.android.com/)
- [Material Design Components](https://material.io/components)

---

**Tài liệu được tạo bởi: AI Assistant**  
**Ngày cập nhật: 2024**  
**Version: 1.0**

