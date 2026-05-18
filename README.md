# Hệ thống Phân công Cán bộ Coi thi (Exam Assignment System)

Đây là ứng dụng Client-Server hỗ trợ tự động phân công Giám thị (Coi thi) và Giám sát hành lang từ một danh sách cán bộ có sẵn trong file Excel.

## 1. Yêu cầu hệ thống chung
Bất kể bạn chạy Server hay Client, máy tính của bạn cần cài đặt sẵn các công cụ sau. Hãy mở Terminal (Command Prompt / PowerShell) để kiểm tra:

### 1.1. Java Development Kit (JDK) 11+
- **Lệnh kiểm tra:**
  ```bash
  java -version
  ```
  *(Nếu hiện ra thông tin phiên bản như "11.x", "17.x", hoặc "21.x" là hợp lệ)*
- **Cách cài đặt (nếu báo lỗi không tìm thấy lệnh):**
  - Truy cập trang chủ [Eclipse Adoptium](https://adoptium.net/) hoặc Oracle để tải bộ cài JDK (khuyến nghị bản JDK 17 hoặc 21).
  - Chạy file cài đặt, nhớ tích chọn mục *"Add to PATH"* hoặc *"Set JAVA_HOME"* trong quá trình cài.

### 1.2. Apache Maven 3.6+
- **Lệnh kiểm tra:**
  ```bash
  mvn -version
  ```
- **Cách cài đặt:**
  - *(Lưu ý: Nếu bạn mở code bằng phần mềm như IntelliJ IDEA hoặc Eclipse thì có thể bỏ qua bước này vì IDE đã tích hợp sẵn Maven).*
  - Nếu cần tự cài: Tải file `.zip` từ [trang chủ Maven](https://maven.apache.org/download.cgi), giải nén và thêm đường dẫn của thư mục `bin` vào biến môi trường `PATH` của Windows.

---

## 2. Hướng dẫn dành cho người chạy SERVER (Backend)
Nếu bạn là người host hệ thống (chạy Server), bạn bắt buộc phải thiết lập Cơ sở dữ liệu và mở cổng mạng.

### 2.1. Cài đặt Cơ sở dữ liệu (Database Setup)
1. Cài đặt **MySQL Server** phiên bản 8.0+ và đảm bảo service đang chạy.
   - **Lệnh kiểm tra:** Mở terminal và gõ `mysql -V` để xem phiên bản, hoặc gõ `mysql -u root -p` để thử đăng nhập.
   - **Cách cài đặt (nếu chưa có):** Bạn có thể tải trực tiếp từ [MySQL Community Server](https://dev.mysql.com/downloads/installer/) hoặc cách nhanh nhất là cài đặt phần mềm **XAMPP** (đã bao gồm sẵn MySQL và công cụ quản lý phpMyAdmin).
2. Mở công cụ quản lý CSDL (như MySQL Workbench, phpMyAdmin, DBeaver) và chạy file `database/schema.sql` để tạo database `exam_assignment_db` cùng các bảng cần thiết.
3. *(Tùy chọn)* Đổi mật khẩu MySQL trong code:
   - Mở file: `src/main/java/server/db/DatabaseManager.java`
   - Sửa biến `DB_USER` và `DB_PASSWORD` cho khớp với máy của bạn (mặc định user `root`, mật khẩu rỗng `""`).

### 2.2. Khởi chạy Server
- **Cách 1 (Windows):** Mở thư mục code, bấm đúp vào file `start-server.bat`.
- **Cách 2 (IDE):** Mở project bằng IntelliJ/Eclipse, chạy hàm `main()` trong class `server.ServerMain`.
- *Lưu ý:* Khi Server báo "Đang lắng nghe trên cổng 9999" nghĩa là đã khởi động thành công.

---

## 3. Hướng dẫn dành cho người chạy CLIENT (Frontend)
Nếu bạn chỉ đóng vai trò người dùng (chạy Client để kết nối vào Server đã có sẵn), bạn **KHÔNG CẦN** cài đặt MySQL hay import database. 

### 3.1. Khởi chạy Client
- **Cách 1 (Windows):** Mở thư mục code, bấm đúp vào file `start-client.bat`.
- **Cách 2 (IDE):** Mở project bằng IntelliJ/Eclipse, chạy hàm `main()` trong class `client.ClientMain`.

### 3.2. Cách sử dụng ứng dụng
1. Mở Client lên, tại mục Host và Port, nhập địa chỉ IP của máy đang chạy Server (nếu chạy chung trên 1 máy thì để `localhost` và port `9999`). Bấm **Kết nối**.
2. Bấm **Chọn file Excel** và trỏ tới file danh sách cán bộ mẫu.
3. Nhập số liệu:
   - `n`: Số lượng tổng cán bộ cần bốc thăm.
   - `m`: Số lượng phòng thi.
   - *(Lưu ý bắt buộc: `2m < n <= 3m`)*
4. Nhấn **GỬI PHÂN CÔNG** và đợi Server xử lý.
5. Xem kết quả ở các Tab và nhấn nút **Xuất DS** để lưu kết quả ra file Excel.

---

## 4. Cấu trúc thư mục tham khảo
- `src/main/java/client/`: Giao diện UI (FlatLaf), logic gửi/nhận socket, đọc/ghi file Excel.
- `src/main/java/server/`: Lắng nghe socket, kết nối MySQL (JDBC), xử lý thuật toán bốc thăm.
- `src/main/java/shared/`: Các Model và Protocol dùng chung để giao tiếp giữa 2 bên.
- `database/`: Chứa file `schema.sql` để khởi tạo database.
