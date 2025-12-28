# 📖 HƯỚNG DẪN HIỂU CODE NHANH NHẤT

## 🗂️ CẤU TRÚC THƯ MỤC TỔNG QUAN

```
FE-moblie/
├── app/src/main/
│   ├── java/com/example/du_an_androidd/    ← CODE JAVA (Logic xử lý)
│   │   ├── Activities/                      ← Màn hình chính (full screen)
│   │   ├── Fragments/                       ← Màn hình con (trong MainActivity)
│   │   ├── Adapters/                        ← Hiển thị danh sách (RecyclerView)
│   │   ├── api/                             ← Kết nối Backend API
│   │   ├── model/                           ← Dữ liệu (Request/Response)
│   │   └── utils/                           ← Tiện ích (Token, Helper)
│   │
│   └── res/                                 ← GIAO DIỆN (XML Layouts)
│       ├── layout/                          ← Giao diện màn hình
│       ├── drawable/                        ← Icons, hình ảnh
│       ├── values/                          ← Strings, colors, themes
│       └── menu/                            ← Menu navigation
```

---

## 📱 PHẦN 1: ACTIVITIES (Màn hình chính)

### 1. **LoginActivity.java** - Màn đăng nhập
**Vị trí**: `java/.../LoginActivity.java`  
**Layout**: `res/layout/activity_login.xml`

**Tác dụng**:
- Màn hình đầu tiên khi mở app
- Xử lý đăng nhập (username + password)
- Lưu JWT token sau khi đăng nhập thành công
- Chuyển sang MainActivity nếu đăng nhập thành công

**Kiến thức áp dụng**:
- `AppCompatActivity` - Activity cơ bản của Android
- `Retrofit` - Gọi API POST `/auth/login`
- `TokenManager` - Lưu token vào SharedPreferences
- `Intent` - Chuyển màn hình

**Liên kết**:
```
LoginActivity 
    ↓ (đăng nhập thành công)
    → TokenManager.saveToken()
    → MainActivity
```

---

### 2. **RegisterActivity.java** - Màn đăng ký
**Vị trí**: `java/.../RegisterActivity.java`  
**Layout**: `res/layout/activity_register.xml`

**Tác dụng**:
- Cho phép user đăng ký tài khoản mới
- Gọi API POST `/auth/register`
- Sau khi đăng ký thành công → quay về LoginActivity

**Kiến thức áp dụng**:
- Form validation (kiểm tra input)
- Retrofit POST request
- Intent để quay lại màn hình trước

---

### 3. **MainActivity.java** - Màn hình chính (Container)
**Vị trí**: `java/.../MainActivity.java`  
**Layout**: `res/layout/activity_main.xml`

**Tác dụng**:
- **Container chính** chứa tất cả các màn hình con (Fragments)
- Có Bottom Navigation (menu dưới đáy)
- Kiểm tra Token trước khi vào (bảo mật)
- Quản lý việc chuyển đổi giữa các Fragments

**Kiến thức áp dụng**:
- `FragmentManager` - Quản lý Fragments
- `BottomNavigationView` - Menu điều hướng
- Fragment Transaction - Thay thế Fragment khi chọn tab

**Liên kết**:
```
MainActivity (Container)
    ├── BookManagementFragment      (Tab: Quản lý sách)
    ├── InvoiceManagementFragment   (Tab: Quản lý mượn/trả)
    ├── CustomerManagementFragment  (Tab: Quản lý khách hàng)
    └── AccountManagementFragment   (Tab: Quản lý tài khoản)
```

**Luồng hoạt động**:
```java
User chọn tab → BottomNavigationView.onItemSelected()
    ↓
Tạo Fragment mới (BookManagementFragment, InvoiceManagementFragment...)
    ↓
FragmentManager.replace(fragment_container, newFragment)
    ↓
Hiển thị Fragment mới
```

---

### 4. **BookDetailActivity.java** - Chi tiết sách
**Vị trí**: `java/.../BookDetailActivity.java`  
**Layout**: `res/layout/activity_book_detail.xml`

**Tác dụng**:
- Hiển thị thông tin chi tiết của 1 cuốn sách
- Cho phép chỉnh sửa thông tin sách
- Load và hiển thị ảnh bìa sách

**Kiến thức áp dụng**:
- `Intent.getSerializableExtra()` - Nhận dữ liệu từ Activity khác
- `Glide` - Load ảnh từ URL
- `AlertDialog` - Dialog chỉnh sửa

**Liên kết**:
```
BookAdapter (click "Chi tiết")
    ↓ Intent.putExtra("book", book)
    → BookDetailActivity
```

---

## 🧩 PHẦN 2: FRAGMENTS (Màn hình con)

### 1. **BookManagementFragment.java** - Quản lý sách
**Vị trí**: `java/.../BookManagementFragment.java`  
**Layout**: `res/layout/fragment_book_management.xml`

**Tác dụng**:
- Hiển thị danh sách sách (dùng RecyclerView)
- Thêm sách mới (FAB button)
- Sửa/Xóa sách
- Xem chi tiết sách

**Kiến thức áp dụng**:
- `Fragment` - Màn hình con trong Activity
- `RecyclerView` + `BookAdapter` - Hiển thị danh sách
- `FloatingActionButton` - Nút thêm mới
- `AlertDialog` - Dialog form thêm/sửa
- Retrofit GET/POST/PUT/DELETE

**Liên kết**:
```
BookManagementFragment
    ├── BookAdapter (hiển thị danh sách)
    │   ├── onEditClick() → showAddBookDialog()
    │   ├── onDeleteClick() → deleteBookApi()
    │   └── onViewMoreClick() → BookDetailActivity
    │
    ├── ApiService.getBooks() → Load danh sách
    ├── ApiService.addBook() → Thêm sách
    └── ApiService.updateBook() → Sửa sách
```

**Luồng hoạt động**:
```java
onCreateView()
    ↓
setupRecyclerView() → Tạo BookAdapter
    ↓
loadBooksFromApi() → Gọi API GET /books
    ↓
bookAdapter.setData(books) → Hiển thị danh sách
```

---

### 2. **InvoiceManagementFragment.java** - Quản lý mượn/trả
**Vị trí**: `java/.../InvoiceManagementFragment.java`  
**Layout**: `res/layout/fragment_invoice_management.xml`

**Tác dụng**:
- Hiển thị danh sách các lần mượn/trả sách
- Cho phép mượn sách mới
- Xử lý trả sách

**Kiến thức áp dụng**:
- RecyclerView với LoanAdapter
- Retrofit POST `/loans/borrow` và `/loans/return`

---

### 3. **CustomerManagementFragment.java** - Quản lý khách hàng
**Vị trí**: `java/.../CustomerManagementFragment.java`  
**Layout**: `res/layout/fragment_customer_management.xml`

**Tác dụng**:
- Hiển thị danh sách khách hàng (members)
- Thêm/Sửa/Xóa khách hàng

**Kiến thức áp dụng**:
- RecyclerView với CustomerAdapter
- CRUD operations với API

---

### 4. **AccountManagementFragment.java** - Quản lý tài khoản
**Vị trí**: `java/.../fragment/AccountManagementFragment.java`  
**Layout**: `res/layout/fragment_account_management.xml`

**Tác dụng**:
- Hiển thị thông tin tài khoản hiện tại
- Nút đăng xuất

**Kiến thức áp dụng**:
- `TokenManager.clearToken()` - Xóa token
- Intent quay về LoginActivity

---

## 📋 PHẦN 3: ADAPTERS (Hiển thị danh sách)

### 1. **BookAdapter.java** - Adapter cho danh sách sách
**Vị trí**: `java/.../BookAdapter.java`  
**Layout item**: `res/layout/item_book.xml`

**Tác dụng**:
- Hiển thị từng item sách trong RecyclerView
- Load ảnh bìa bằng Glide
- Xử lý click events (Edit, Delete, View More)

**Kiến thức áp dụng**:
- `RecyclerView.Adapter` - Pattern để hiển thị danh sách
- `ViewHolder Pattern` - Giữ reference đến Views
- `Glide` - Load ảnh async
- `onBindViewHolder()` - Bind data vào View

**Cấu trúc**:
```java
BookAdapter
    ├── BookViewHolder (giữ reference đến Views)
    ├── onBindViewHolder() (hiển thị dữ liệu)
    └── OnBookClickListener (interface cho events)
        ├── onEditClick()
        ├── onDeleteClick()
        └── onViewMoreClick()
```

**Liên kết**:
```
BookManagementFragment
    ↓ new BookAdapter(listener)
    ↓ RecyclerView.setAdapter(bookAdapter)
    ↓ bookAdapter.setData(books)
    → Hiển thị danh sách sách
```

---

### 2. **CustomerAdapter.java** - Adapter cho danh sách khách hàng
**Tương tự BookAdapter**, nhưng cho Members

---

### 3. **LoanAdapter.java** - Adapter cho danh sách mượn/trả
**Tương tự**, nhưng cho Loans

---

## 🌐 PHẦN 4: API (Kết nối Backend)

### 1. **ApiClient.java** - Cấu hình Retrofit
**Vị trí**: `java/.../api/ApiClient.java`

**Tác dụng**:
- Tạo và cấu hình Retrofit instance
- Thêm JWT token vào mọi request (Interceptor)
- Set timeout cho requests
- Log requests/responses để debug

**Kiến thức áp dụng**:
- `Retrofit.Builder` - Tạo Retrofit client
- `OkHttpClient` - HTTP client với interceptors
- `Interceptor` - Tự động thêm headers (Authorization)
- `GsonConverterFactory` - Convert JSON ↔ Java objects

**Code mẫu**:
```java
ApiClient.getService(context)
    ↓
Tạo OkHttpClient với Interceptor (thêm Token)
    ↓
Tạo Retrofit với BASE_URL + GsonConverter
    ↓
Return ApiService instance
```

**Liên kết**:
```
Mọi Fragment/Activity
    ↓ ApiClient.getService(context)
    → ApiService (để gọi API)
```

---

### 2. **ApiService.java** - Định nghĩa API Endpoints
**Vị trí**: `java/.../api/ApiService.java`

**Tác dụng**:
- Định nghĩa tất cả các API endpoints (GET, POST, PUT, DELETE)
- Khai báo request/response types

**Kiến thức áp dụng**:
- Retrofit annotations: `@GET`, `@POST`, `@PUT`, `@DELETE`
- `@Query` - Query parameters (?page=1&limit=10)
- `@Path` - Path parameters (/books/{id})
- `@Body` - Request body (JSON)
- `Call<T>` - Async call

**Ví dụ**:
```java
@GET("books")
Call<ApiResponse<List<Book>>> getBooks(
    @Query("page") int page, 
    @Query("limit") int limit
);
```

**Liên kết**:
```
ApiService
    ├── login() → POST /auth/login
    ├── getBooks() → GET /books
    ├── addBook() → POST /books
    ├── updateBook() → PUT /books/{id}
    └── deleteBook() → DELETE /books/{id}
```

---

## 📦 PHẦN 5: MODELS (Dữ liệu)

### 1. **Request Models** - Dữ liệu gửi lên server
**Vị trí**: `java/.../model/request/`

**Các file**:
- `BookRequest.java` - Dữ liệu khi thêm/sửa sách
- `LoginRequest.java` - Username + password
- `LoanRequest.java` - Dữ liệu khi mượn sách
- `MemberRequest.java` - Dữ liệu khi thêm/sửa khách hàng

**Kiến thức áp dụng**:
- `@SerializedName` - Map JSON key → Java field
- Gson tự động serialize thành JSON khi gửi lên server

**Ví dụ**:
```java
public class BookRequest {
    @SerializedName("title")
    private String title;
    
    @SerializedName("category_id")
    private int categoryId;
}
```

---

### 2. **Response Models** - Dữ liệu nhận từ server
**Vị trí**: `java/.../model/response/`

**Các file**:
- `Book.java` - Thông tin sách
- `Author.java` - Thông tin tác giả
- `Loan.java` - Thông tin mượn/trả
- `Member.java` - Thông tin khách hàng
- `LoginResponse.java` - Token sau khi đăng nhập

**Kiến thức áp dụng**:
- `Serializable` - Cho phép truyền qua Intent
- Gson tự động deserialize JSON → Java object

---

### 3. **ApiResponse.java** - Wrapper cho response
**Vị trí**: `java/.../model/ApiResponse.java`

**Tác dụng**:
- Server luôn trả về format: `{success: true, message: "...", data: {...}}`
- Generic `<T>` để dùng cho nhiều loại data

**Ví dụ**:
```java
ApiResponse<Book> response = ...
if (response.isSuccess()) {
    Book book = response.getData();
}
```

---

## 🛠️ PHẦN 6: UTILS (Tiện ích)

### 1. **TokenManager.java** - Quản lý JWT Token
**Vị trí**: `java/.../utils/TokenManager.java`

**Tác dụng**:
- Lưu JWT token vào SharedPreferences
- Đọc token khi cần
- Xóa token khi đăng xuất

**Kiến thức áp dụng**:
- `SharedPreferences` - Lưu dữ liệu key-value vào file XML
- Singleton pattern (không cần tạo nhiều instance)

**Liên kết**:
```
LoginActivity
    ↓ TokenManager.saveToken(token)
    → SharedPreferences (lưu token)
    
ApiClient
    ↓ TokenManager.getToken()
    → Thêm vào Header: "Authorization: Bearer {token}"
```

---

## 🎨 PHẦN 7: RESOURCES (Giao diện)

### 1. **Layout Files** - Giao diện màn hình
**Vị trí**: `res/layout/`

**Các file quan trọng**:
- `activity_login.xml` - Giao diện đăng nhập
- `activity_main.xml` - Container chính (có Bottom Nav)
- `fragment_book_management.xml` - Giao diện quản lý sách
- `item_book.xml` - Giao diện 1 item sách trong danh sách
- `dialog_add_book.xml` - Dialog form thêm/sửa sách

**Kiến thức áp dụng**:
- XML Layout với các View: TextView, EditText, Button, RecyclerView
- `findViewById()` - Lấy reference đến View trong code Java

---

### 2. **Drawable** - Icons, hình ảnh
**Vị trí**: `res/drawable/`

**Các file**:
- `ic_book.xml` - Icon sách
- `ic_person.xml` - Icon người
- `ic_delete.xml` - Icon xóa
- `ic_edit.xml` - Icon sửa

---

### 3. **Values** - Strings, Colors, Themes
**Vị trí**: `res/values/`

- `strings.xml` - Tất cả text hiển thị
- `colors.xml` - Màu sắc
- `themes.xml` - Theme của app

---

## 🔄 LUỒNG HOẠT ĐỘNG TỔNG QUAN

### Luồng Đăng nhập:
```
App khởi động
    ↓
LoginActivity (màn đầu tiên)
    ↓ User nhập username/password
    ↓ Click "Đăng nhập"
    ↓
ApiService.login(LoginRequest)
    ↓ Retrofit POST /auth/login
    ↓
Backend API
    ↓ Response: {token: "abc123"}
    ↓
TokenManager.saveToken(token)
    ↓
Intent → MainActivity
    ↓
MainActivity.onCreate()
    ↓ Kiểm tra Token (bảo mật)
    ↓
Hiển thị BookManagementFragment
```

---

### Luồng Xem danh sách sách:
```
MainActivity
    ↓ User chọn tab "Quản lý sách"
    ↓
BookManagementFragment.onCreateView()
    ↓
setupRecyclerView()
    ↓ new BookAdapter(listener)
    ↓
loadBooksFromApi()
    ↓ ApiService.getBooks(1, 100)
    ↓ Retrofit GET /books?page=1&limit=100
    ↓
Backend API
    ↓ Response: ApiResponse<List<Book>>
    ↓
bookAdapter.setData(books)
    ↓
RecyclerView hiển thị danh sách
```

---

### Luồng Thêm sách:
```
BookManagementFragment
    ↓ User click FAB (nút +)
    ↓
showAddBookDialog(null)
    ↓ Inflate dialog_add_book.xml
    ↓
User nhập thông tin → Click "Lưu sách"
    ↓
Tạo BookRequest từ dữ liệu
    ↓
ApiService.addBook(BookRequest)
    ↓ Retrofit POST /books
    ↓
Backend API
    ↓ Response: ApiResponse<Book>
    ↓
loadBooksFromApi() → Refresh danh sách
```

---

### Luồng Xem chi tiết sách:
```
BookAdapter
    ↓ User click "Chi tiết"
    ↓
listener.onViewMoreClick(book)
    ↓
BookManagementFragment.onViewMoreClick()
    ↓
Intent → BookDetailActivity
    ↓ Intent.putExtra("book", book)
    ↓
BookDetailActivity.onCreate()
    ↓
displayBookDetails(book)
    ↓
Hiển thị thông tin + ảnh bìa
```

---

## 🔗 SƠ ĐỒ LIÊN KẾT GIỮA CÁC FILE

```
┌─────────────────────────────────────────────────────────┐
│                    LoginActivity                        │
│  - activity_login.xml                                   │
│  - ApiService.login()                                   │
│  - TokenManager.saveToken()                             │
└──────────────────┬──────────────────────────────────────┘
                   │ (Intent)
                   ↓
┌─────────────────────────────────────────────────────────┐
│                    MainActivity                         │
│  - activity_main.xml (Container)                       │
│  - BottomNavigationView                                 │
│  - FragmentManager                                      │
└──┬──────────┬──────────┬──────────┬───────────────────┘
   │          │          │          │
   ↓          ↓          ↓          ↓
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│ Book    │ │Invoice │ │Customer │ │Account  │
│Fragment │ │Fragment│ │Fragment │ │Fragment │
└────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘
     │          │          │          │
     ↓          ↓          ↓          ↓
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
│Book     │ │Loan     │ │Customer │ │Token    │
│Adapter  │ │Adapter  │ │Adapter  │ │Manager  │
└────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘
     │          │          │          │
     ↓          ↓          ↓          ↓
┌─────────────────────────────────────────────────────────┐
│                    ApiService                           │
│  - getBooks()                                           │
│  - addBook()                                            │
│  - updateBook()                                         │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ↓
┌─────────────────────────────────────────────────────────┐
│                    ApiClient                            │
│  - Retrofit configuration                               │
│  - OkHttpClient + Interceptor (Token)                   │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ↓
            Backend API (Node.js)
```

---

## 📚 KIẾN THỨC ÁP DỤNG TRONG TỪNG FILE

### **Activities & Fragments**:
- **Lifecycle**: `onCreate()`, `onCreateView()`, `onDestroy()`
- **View Binding**: `findViewById()`, `setContentView()`
- **Navigation**: `Intent`, `FragmentTransaction`
- **Async Operations**: Retrofit `enqueue()` (không chặn UI thread)

### **Adapters**:
- **RecyclerView Pattern**: `Adapter`, `ViewHolder`, `onBindViewHolder()`
- **View Recycling**: Tái sử dụng Views để tiết kiệm memory

### **API**:
- **Retrofit**: Type-safe HTTP client
- **Gson**: JSON serialization/deserialization
- **OkHttp Interceptors**: Thêm headers tự động
- **Callbacks**: `onResponse()`, `onFailure()`

### **Models**:
- **Serialization**: `@SerializedName` cho Gson
- **Generics**: `ApiResponse<T>` để tái sử dụng
- **Data Classes**: Chỉ chứa data, không có logic

### **Utils**:
- **SharedPreferences**: Lưu dữ liệu nhỏ (key-value)
- **Singleton Pattern**: Chỉ 1 instance của TokenManager

---

## 🎯 CÁCH ĐỌC CODE NHANH NHẤT

### **Bước 1: Hiểu cấu trúc tổng quan**
1. Xem `MainActivity` → Hiểu cách app điều hướng
2. Xem `LoginActivity` → Hiểu luồng đăng nhập
3. Xem `BookManagementFragment` → Hiểu cách 1 màn hình hoạt động

### **Bước 2: Hiểu từng component**
1. **Activity/Fragment**: Xem `onCreate()` → Hiểu setup
2. **Adapter**: Xem `onBindViewHolder()` → Hiểu cách hiển thị data
3. **API**: Xem `ApiService` → Hiểu các endpoints
4. **Model**: Xem các class → Hiểu cấu trúc dữ liệu

### **Bước 3: Trace luồng dữ liệu**
1. User action → Fragment/Activity
2. Fragment → ApiService
3. ApiService → Backend API
4. Response → Update UI

---

## 💡 MẸO ĐỌC CODE

1. **Bắt đầu từ MainActivity**: Đây là entry point chính
2. **Follow the flow**: User click → Event handler → API call → Update UI
3. **Xem imports**: Biết file này dùng file nào
4. **Xem layout XML**: Hiểu giao diện trước, rồi mới xem logic
5. **Debug bằng Log**: Thêm `Log.d()` để xem flow

---

**Tài liệu này sẽ giúp bạn hiểu code nhanh nhất!** 🚀

