# HƯỚNG DẪN CHẠY PROJECT
## Hệ thống Phân công Cán bộ Coi thi - Client/Server

---

## 📋 YÊU CẦU HỆ THỐNG

| Phần mềm | Phiên bản | Ghi chú |
|-----------|-----------|---------|
| **Java JDK** | 11 trở lên (đã cài JDK 24) | Kiểm tra: `java -version` |
| **MySQL Server** | 5.7 trở lên | Chạy trên `localhost:3306` |
| **Maven** | Đã tải sẵn tại `C:\maven-399` | Không cần cài thêm |

---

## 🚀 HƯỚNG DẪN TỪNG BƯỚC

### Bước 1: Cài đặt và khởi động MySQL

1. Đảm bảo **MySQL** đã được cài đặt và đang chạy
2. Mặc định hệ thống kết nối với:
   - **Host:** localhost
   - **Port:** 3306
   - **User:** root
   - **Password:** _(để trống)_

> ⚠️ Nếu MySQL của bạn dùng **password khác**, hãy sửa file:
> `src/main/java/server/db/DatabaseManager.java`
> Tìm dòng `DB_PASSWORD = ""` và thay bằng password của bạn.

### Bước 2: Biên dịch (Compile) Project

Mở **PowerShell** hoặc **CMD** tại thư mục project `TH_LTM`, chạy:

```powershell
# PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
& "C:\maven-399\bin\mvn.cmd" compile
```

```cmd
# CMD
set JAVA_HOME=C:\Program Files\Java\jdk-24
C:\maven-399\bin\mvn.cmd compile
```

Kết quả mong đợi: **BUILD SUCCESS**

### Bước 3: Khởi động Server

Mở **một cửa sổ CMD mới**, chạy:

```cmd
cd "c:\Users\Admin\OneDrive - The University of Technology\Kì 6\Lập trình Mạng\TH_LTM"
.\start-server.bat
```

Hoặc chạy trực tiếp bằng Maven:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
& "C:\maven-399\bin\mvn.cmd" exec:java "-Dexec.mainClass=server.ServerMain"
```

Server khởi động thành công sẽ hiển thị:
```
===========================================
  HE THONG PHAN CONG CAN BO COI THI
  SERVER - Port 9999
===========================================
[DB] Ket noi MySQL thanh cong!
[DB] Cac bang da duoc tao/kiem tra thanh cong.
[Server] Database da san sang.
[Server] Dang lang nghe tren port 9999...
```

> ⚠️ **Giữ cửa sổ Server mở** trong suốt quá trình sử dụng!

### Bước 4: Khởi động Client

Mở **một cửa sổ CMD mới khác**, chạy:

```cmd
cd "c:\Users\Admin\OneDrive - The University of Technology\Kì 6\Lập trình Mạng\TH_LTM"
.\start-client.bat
```

Hoặc chạy trực tiếp bằng Maven:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
& "C:\maven-399\bin\mvn.cmd" exec:java "-Dexec.mainClass=client.ClientMain"
```

Giao diện Client (Java Swing) sẽ mở ra.

### Bước 5: Sử dụng hệ thống

#### 5.1 Kết nối đến Server
- **Host:** `localhost`
- **Port:** `9999`
- Nhấn nút **"Ket noi"**
- Thanh trạng thái hiển thị: `● Da ket noi: localhost:9999`

#### 5.2 Chọn file Excel đầu vào
- Nhấn nút **"[+] Chon file Excel"**
- Chọn file `.xlsx` (dùng file mẫu `sample/DuLieuMau.xlsx` để test)
- Hệ thống sẽ hiển thị preview dữ liệu trên 2 tab:
  - Tab "Danh sach Can bo"
  - Tab "Danh sach Phong thi"

#### 5.3 Gửi yêu cầu phân công
- Nhấn nút **">>> GUI PHAN CONG"**
- Server xử lý và trả kết quả
- Kết quả hiển thị bên phải trên 2 tab:
  - Tab "Giam thi" — danh sách phân công giám thị
  - Tab "Giam sat Hanh lang" — danh sách giám sát hành lang

#### 5.4 Xuất file Excel kết quả
- Nhấn **"[v] Xuat DS Coi thi"** → Lưu file phân công coi thi
- Nhấn **"[v] Xuat DS Giam sat"** → Lưu file giám sát hành lang
- Chọn vị trí lưu file → Done!

---

## 📁 FILE EXCEL MẪU

File mẫu nằm tại: `sample/DuLieuMau.xlsx`

**Sheet 1 - Danh sách cán bộ** (20 người):

| ID | Mã GV | Họ Tên | Ngày Sinh | Đơn vị |
|----|-------|--------|-----------|--------|
| 1 | GV001 | Nguyen Van An | | Khoa CNTT |
| 2 | GV002 | Tran Thi Binh | | Khoa CNTT |
| ... | ... | ... | ... | ... |

**Sheet 2 - Danh sách phòng thi** (8 phòng):

| STT | Phòng Thi | Ghi Chú |
|-----|-----------|---------|
| 1 | A101 | Co so 1 - TP.HCM |
| 2 | A102 | Co so 1 - TP.HCM |
| ... | ... | ... |

Kết quả: 16 cán bộ làm giám thị + 4 cán bộ giám sát hành lang.

---

## 📂 CẤU TRÚC PROJECT

```
TH_LTM/
├── pom.xml                              # Maven config
├── start-server.bat                     # Script chạy Server
├── start-client.bat                     # Script chạy Client
├── database/
│   └── schema.sql                       # SQL tạo database
├── sample/
│   └── DuLieuMau.xlsx                   # File test
└── src/main/java/
    ├── common/
    │   ├── model/                       # Các model dùng chung
    │   │   ├── CanBo.java
    │   │   ├── PhongThi.java
    │   │   ├── PhanCong.java
    │   │   ├── GiamSat.java
    │   │   ├── RequestData.java
    │   │   └── ResultData.java
    │   └── protocol/                    # Protocol giao tiếp
    │       ├── Message.java
    │       └── MessageType.java
    ├── server/                          # Server
    │   ├── ServerMain.java              # Entry point
    │   ├── ClientHandler.java           # Xử lý client
    │   ├── db/
    │   │   └── DatabaseManager.java     # JDBC MySQL
    │   └── service/
    │       └── AssignmentService.java   # Thuật toán phân công
    ├── client/                          # Client
    │   ├── ClientMain.java              # Entry point
    │   ├── ClientConnection.java        # Socket connection
    │   ├── ui/
    │   │   ├── MainFrame.java           # Giao diện chính
    │   │   └── UIHelper.java            # Theme & styles
    │   └── excel/
    │       ├── ExcelReader.java         # Đọc Excel
    │       └── ExcelWriter.java         # Xuất Excel
    └── util/
        └── SampleDataGenerator.java     # Tạo data mẫu
```

---

## 🗄️ CƠ SỞ DỮ LIỆU

Database: `exam_assignment_db` — Tự động tạo khi Server khởi động.

| Bảng | Mô tả |
|------|--------|
| `CanBo` | Danh sách cán bộ coi thi |
| `PhongThi` | Danh sách phòng thi |
| `DotPhanCong` | Thông tin mỗi đợt phân công |
| `PhanCong` | Kết quả phân công giám thị |
| `GiamSat` | Danh sách giám sát hành lang |

Nếu muốn tạo thủ công, chạy file `database/schema.sql` trên MySQL.

---

## ❓ XỬ LÝ LỖI THƯỜNG GẶP

### 1. "Khong the ket noi database"
- Kiểm tra MySQL đã chạy chưa
- Kiểm tra user/password MySQL
- Sửa `DB_PASSWORD` trong `DatabaseManager.java`

### 2. "start-server.bat is not recognized"
- Trong **PowerShell**, cần thêm `.\` phía trước:
  ```powershell
  .\start-server.bat
  ```
- Hoặc dùng **CMD** thay vì PowerShell

### 3. "Khong du can bo coi thi"
- Số cán bộ phải >= 2 × số phòng thi
- Kiểm tra lại file Excel đầu vào

### 4. "Connection refused" khi Client kết nối
- Đảm bảo Server đã khởi động trước
- Kiểm tra port 9999 không bị chặn bởi firewall

### 5. Lỗi compile
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
& "C:\maven-399\bin\mvn.cmd" clean compile
```

---

## 🔧 THAY ĐỔI CẤU HÌNH

| Cấu hình | File | Dòng |
|-----------|------|------|
| Port Server | `ServerMain.java` | `PORT = 9999` |
| MySQL Host | `DatabaseManager.java` | `DB_URL` |
| MySQL User | `DatabaseManager.java` | `DB_USER` |
| MySQL Password | `DatabaseManager.java` | `DB_PASSWORD` |
| Tên database | `DatabaseManager.java` | `exam_assignment_db` |
