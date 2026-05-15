-- =====================================================
-- HỆ THỐNG PHÂN CÔNG CÁN BỘ COI THI
-- Database Schema - MySQL
-- =====================================================

CREATE DATABASE IF NOT EXISTS exam_assignment_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE exam_assignment_db;

-- =====================================================
-- Bảng Cán bộ coi thi
-- =====================================================
CREATE TABLE IF NOT EXISTS CanBo (
    id INT PRIMARY KEY,
    ma_gv VARCHAR(20) NOT NULL,
    ho_ten NVARCHAR(100) NOT NULL,
    ngay_sinh DATE,
    don_vi NVARCHAR(200),
    UNIQUE KEY uk_ma_gv (ma_gv)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Bảng Phòng thi
-- =====================================================
CREATE TABLE IF NOT EXISTS PhongThi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    phong_thi VARCHAR(50) NOT NULL,
    ghi_chu NVARCHAR(200),
    UNIQUE KEY uk_phong_thi (phong_thi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Bảng Đợt phân công
-- =====================================================
CREATE TABLE IF NOT EXISTS DotPhanCong (
    id INT AUTO_INCREMENT PRIMARY KEY,
    thoi_gian_tao DATETIME DEFAULT CURRENT_TIMESTAMP,
    so_phong INT NOT NULL,
    so_can_bo INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Bảng Phân công giám thị
-- =====================================================
CREATE TABLE IF NOT EXISTS PhanCong (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dot_id INT NOT NULL,
    phong_thi_id INT NOT NULL,
    giam_thi_1_id INT NOT NULL,
    giam_thi_2_id INT NOT NULL,
    FOREIGN KEY (dot_id) REFERENCES DotPhanCong(id) ON DELETE CASCADE,
    FOREIGN KEY (phong_thi_id) REFERENCES PhongThi(id) ON DELETE CASCADE,
    FOREIGN KEY (giam_thi_1_id) REFERENCES CanBo(id) ON DELETE CASCADE,
    FOREIGN KEY (giam_thi_2_id) REFERENCES CanBo(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Bảng Giám sát hành lang
-- =====================================================
CREATE TABLE IF NOT EXISTS GiamSat (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dot_id INT NOT NULL,
    can_bo_id INT NOT NULL,
    tu_phong VARCHAR(50),
    den_phong VARCHAR(50),
    FOREIGN KEY (dot_id) REFERENCES DotPhanCong(id) ON DELETE CASCADE,
    FOREIGN KEY (can_bo_id) REFERENCES CanBo(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
