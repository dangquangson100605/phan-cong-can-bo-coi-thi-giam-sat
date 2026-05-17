package server.db;

import common.model.*;

import java.sql.*;
import java.util.*;

/**
 * Quản lý kết nối và thao tác CSDL MySQL.
 * Sử dụng Singleton pattern.
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/exam_assignment_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private DatabaseManager() {}

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void connect() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("[DB] Ket noi MySQL thanh cong!");
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Da dong ket noi MySQL.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void initializeDatabase() throws SQLException {
        try (Connection tempConn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8",
                DB_USER, DB_PASSWORD)) {
            Statement stmt = tempConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS exam_assignment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
        connect();
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CanBo (id INT AUTO_INCREMENT PRIMARY KEY, ma_gv VARCHAR(20) NOT NULL, ho_ten VARCHAR(100) NOT NULL, ngay_sinh DATE, don_vi VARCHAR(200), UNIQUE KEY uk_ma_gv (ma_gv)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PhongThi (id INT AUTO_INCREMENT PRIMARY KEY, phong_thi VARCHAR(50) NOT NULL, ghi_chu VARCHAR(200), UNIQUE KEY uk_phong_thi (phong_thi)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS DotPhanCong (id INT AUTO_INCREMENT PRIMARY KEY, thoi_gian_tao DATETIME DEFAULT CURRENT_TIMESTAMP, so_phong INT NOT NULL, so_can_bo INT NOT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS PhanCong (id INT AUTO_INCREMENT PRIMARY KEY, dot_id INT NOT NULL, phong_thi_id INT NOT NULL, giam_thi_1_id INT NOT NULL, giam_thi_2_id INT NOT NULL, FOREIGN KEY (dot_id) REFERENCES DotPhanCong(id) ON DELETE CASCADE, FOREIGN KEY (phong_thi_id) REFERENCES PhongThi(id) ON DELETE CASCADE, FOREIGN KEY (giam_thi_1_id) REFERENCES CanBo(id) ON DELETE CASCADE, FOREIGN KEY (giam_thi_2_id) REFERENCES CanBo(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS GiamSat (id INT AUTO_INCREMENT PRIMARY KEY, dot_id INT NOT NULL, can_bo_id INT NOT NULL, tu_phong VARCHAR(50), den_phong VARCHAR(50), dia_diem VARCHAR(200), FOREIGN KEY (dot_id) REFERENCES DotPhanCong(id) ON DELETE CASCADE, FOREIGN KEY (can_bo_id) REFERENCES CanBo(id) ON DELETE CASCADE) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
            try {
                stmt.executeUpdate("ALTER TABLE GiamSat ADD COLUMN dia_diem VARCHAR(200)");
            } catch (SQLException e) {
                // Ignore if column already exists
            }
            System.out.println("[DB] Cac bang da duoc tao/kiem tra thanh cong.");
        }
    }

    /**
     * Luu danh sach can bo vao DB. Su dung ma_gv lam khoa nghiep vu (UNIQUE KEY).
     * Tra ve danh sach CanBo voi id thuc te tu DB (AUTO_INCREMENT).
     */
    public List<CanBo> saveCanBoList(List<CanBo> canBoList) throws SQLException {
        String insertSql = "INSERT INTO CanBo (ma_gv, ho_ten, ngay_sinh, don_vi) VALUES (?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE ho_ten=VALUES(ho_ten), ngay_sinh=VALUES(ngay_sinh), don_vi=VALUES(don_vi)";

        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            try (PreparedStatement insPs = connection.prepareStatement(insertSql)) {
                for (CanBo cb : canBoList) {
                    insPs.setString(1, cb.getMaGV());
                    insPs.setString(2, cb.getHoTen());
                    if (cb.getNgaySinh() != null) {
                        insPs.setDate(3, new java.sql.Date(cb.getNgaySinh().getTime()));
                    } else {
                        insPs.setNull(3, Types.DATE);
                    }
                    insPs.setString(4, cb.getDonVi());
                    insPs.addBatch();
                }
                insPs.executeBatch();
            }

            connection.commit();
            System.out.println("[DB] Da luu " + canBoList.size() + " can bo.");
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("[DB] LOI khi luu can bo: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }

        // Tai lai danh sach can bo tu DB de lay id thuc te (AUTO_INCREMENT)
        return reloadCanBoByMaGV(canBoList);
    }

    /**
     * Tai lai danh sach can bo tu DB theo ma_gv de lay dung id (AUTO_INCREMENT).
     * Su dung 1 query duy nhat de toi uu voi du lieu lon.
     */
    private List<CanBo> reloadCanBoByMaGV(List<CanBo> canBoList) throws SQLException {
        // Tai tat ca can bo tu DB 1 lan, map theo ma_gv
        Map<String, CanBo> dbMap = new HashMap<>();
        String selectAll = "SELECT id, ma_gv, ho_ten, ngay_sinh, don_vi FROM CanBo";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAll)) {
            while (rs.next()) {
                CanBo cb = new CanBo(
                    rs.getInt("id"),
                    rs.getString("ma_gv"),
                    rs.getString("ho_ten"),
                    rs.getDate("ngay_sinh"),
                    rs.getString("don_vi")
                );
                dbMap.put(cb.getMaGV(), cb);
            }
        }

        // Map lai theo thu tu danh sach input
        List<CanBo> result = new ArrayList<>();
        for (CanBo cb : canBoList) {
            CanBo fromDb = dbMap.get(cb.getMaGV());
            if (fromDb != null) {
                result.add(fromDb);
            }
        }
        System.out.println("[DB] Da tai lai " + result.size() + " can bo tu DB.");
        return result;
    }

    public List<PhongThi> savePhongThiList(List<PhongThi> phongThiList) throws SQLException {
        String sql = "INSERT INTO PhongThi (phong_thi, ghi_chu) VALUES (?, ?) ON DUPLICATE KEY UPDATE ghi_chu=VALUES(ghi_chu)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (PhongThi pt : phongThiList) {
                ps.setString(1, pt.getPhongThi());
                ps.setString(2, pt.getGhiChu());
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // Tai lai tat ca phong thi tu DB 1 lan, map theo ten phong
        Map<String, PhongThi> dbMap = new HashMap<>();
        String selectAll = "SELECT id, phong_thi, ghi_chu FROM PhongThi";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectAll)) {
            while (rs.next()) {
                PhongThi pt = new PhongThi(rs.getInt("id"), rs.getString("phong_thi"), rs.getString("ghi_chu"));
                dbMap.put(pt.getPhongThi(), pt);
            }
        }

        List<PhongThi> result = new ArrayList<>();
        for (PhongThi pt : phongThiList) {
            PhongThi fromDb = dbMap.get(pt.getPhongThi());
            if (fromDb != null) {
                result.add(fromDb);
            }
        }
        System.out.println("[DB] Da luu " + result.size() + " phong thi.");
        return result;
    }

    public int createDotPhanCong(int soPhong, int soCanBo) throws SQLException {
        String sql = "INSERT INTO DotPhanCong (so_phong, so_can_bo) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, soPhong);
            ps.setInt(2, soCanBo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int dotId = rs.getInt(1);
                    System.out.println("[DB] Tao dot phan cong moi, ID = " + dotId);
                    return dotId;
                }
            }
        }
        throw new SQLException("Khong the tao dot phan cong");
    }

    public void savePhanCong(int dotId, List<PhanCong> phanCongList) throws SQLException {
        String sql = "INSERT INTO PhanCong (dot_id, phong_thi_id, giam_thi_1_id, giam_thi_2_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (PhanCong pc : phanCongList) {
                ps.setInt(1, dotId);
                ps.setInt(2, pc.getPhongThi().getId());
                ps.setInt(3, pc.getGiamThi1().getId());
                ps.setInt(4, pc.getGiamThi2().getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("[DB] Da luu " + phanCongList.size() + " phan cong giam thi.");
    }

    public void saveGiamSat(int dotId, List<GiamSat> giamSatList) throws SQLException {
        String sql = "INSERT INTO GiamSat (dot_id, can_bo_id, tu_phong, den_phong, dia_diem) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (GiamSat gs : giamSatList) {
                ps.setInt(1, dotId);
                ps.setInt(2, gs.getCanBo().getId());
                ps.setString(3, gs.getTuPhong());
                ps.setString(4, gs.getDenPhong());
                ps.setString(5, gs.getDiaDiem());
                ps.addBatch();
            }
            ps.executeBatch();
        }
        System.out.println("[DB] Da luu " + giamSatList.size() + " giam sat hanh lang.");
    }

    public Map<Integer, Set<Integer>> getHistoryRooms() throws SQLException {
        Map<Integer, Set<Integer>> history = new HashMap<>();
        String sql = "SELECT giam_thi_1_id, giam_thi_2_id, phong_thi_id FROM PhanCong";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int gt1 = rs.getInt("giam_thi_1_id");
                int gt2 = rs.getInt("giam_thi_2_id");
                int phong = rs.getInt("phong_thi_id");
                history.computeIfAbsent(gt1, k -> new HashSet<>()).add(phong);
                history.computeIfAbsent(gt2, k -> new HashSet<>()).add(phong);
            }
        }
        return history;
    }

    public Set<String> getHistoryPairs() throws SQLException {
        Set<String> pairs = new HashSet<>();
        String sql = "SELECT giam_thi_1_id, giam_thi_2_id FROM PhanCong";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int gt1 = rs.getInt("giam_thi_1_id");
                int gt2 = rs.getInt("giam_thi_2_id");
                pairs.add(Math.min(gt1, gt2) + "-" + Math.max(gt1, gt2));
            }
        }
        return pairs;
    }

    public List<Map<String, Object>> getAllDotPhanCong() throws SQLException {
        List<Map<String, Object>> dots = new ArrayList<>();
        String sql = "SELECT id, thoi_gian_tao, so_phong, so_can_bo FROM DotPhanCong ORDER BY id DESC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> dot = new HashMap<>();
                dot.put("id", rs.getInt("id"));
                dot.put("thoi_gian_tao", rs.getTimestamp("thoi_gian_tao"));
                dot.put("so_phong", rs.getInt("so_phong"));
                dot.put("so_can_bo", rs.getInt("so_can_bo"));
                dots.add(dot);
            }
        }
        return dots;
    }

    public List<PhanCong> getPhanCongByDot(int dotId) throws SQLException {
        List<PhanCong> result = new ArrayList<>();
        String sql = "SELECT pc.id, pc.dot_id, pt.id as pt_id, pt.phong_thi, pt.ghi_chu, " +
                     "cb1.id as cb1_id, cb1.ma_gv as cb1_magv, cb1.ho_ten as cb1_hoten, cb1.ngay_sinh as cb1_ns, cb1.don_vi as cb1_dv, " +
                     "cb2.id as cb2_id, cb2.ma_gv as cb2_magv, cb2.ho_ten as cb2_hoten, cb2.ngay_sinh as cb2_ns, cb2.don_vi as cb2_dv " +
                     "FROM PhanCong pc JOIN PhongThi pt ON pc.phong_thi_id = pt.id " +
                     "JOIN CanBo cb1 ON pc.giam_thi_1_id = cb1.id JOIN CanBo cb2 ON pc.giam_thi_2_id = cb2.id " +
                     "WHERE pc.dot_id = ? ORDER BY pt.phong_thi";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhongThi pt = new PhongThi(rs.getInt("pt_id"), rs.getString("phong_thi"), rs.getString("ghi_chu"));
                    CanBo cb1 = new CanBo(rs.getInt("cb1_id"), rs.getString("cb1_magv"), rs.getString("cb1_hoten"), rs.getDate("cb1_ns"), rs.getString("cb1_dv"));
                    CanBo cb2 = new CanBo(rs.getInt("cb2_id"), rs.getString("cb2_magv"), rs.getString("cb2_hoten"), rs.getDate("cb2_ns"), rs.getString("cb2_dv"));
                    PhanCong pc = new PhanCong(rs.getInt("dot_id"), pt, cb1, cb2);
                    pc.setId(rs.getInt("id"));
                    result.add(pc);
                }
            }
        }
        return result;
    }

    public List<GiamSat> getGiamSatByDot(int dotId) throws SQLException {
        List<GiamSat> result = new ArrayList<>();
        String sql = "SELECT gs.id, gs.dot_id, gs.tu_phong, gs.den_phong, gs.dia_diem, cb.id as cb_id, cb.ma_gv, cb.ho_ten, cb.ngay_sinh, cb.don_vi " +
                     "FROM GiamSat gs JOIN CanBo cb ON gs.can_bo_id = cb.id WHERE gs.dot_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CanBo cb = new CanBo(rs.getInt("cb_id"), rs.getString("ma_gv"), rs.getString("ho_ten"), rs.getDate("ngay_sinh"), rs.getString("don_vi"));
                    GiamSat gs = new GiamSat(rs.getInt("dot_id"), cb, rs.getString("tu_phong"), rs.getString("den_phong"), rs.getString("dia_diem"));
                    gs.setId(rs.getInt("id"));
                    result.add(gs);
                }
            }
        }
        return result;
    }
}
