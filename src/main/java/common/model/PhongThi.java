package common.model;

import java.io.Serializable;

/**
 * Model đại diện cho Phòng thi
 */
public class PhongThi implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String phongThi;
    private String ghiChu;

    public PhongThi() {}

    public PhongThi(int id, String phongThi, String ghiChu) {
        this.id = id;
        this.phongThi = phongThi;
        this.ghiChu = ghiChu;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPhongThi() { return phongThi; }
    public void setPhongThi(String phongThi) { this.phongThi = phongThi; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    @Override
    public String toString() {
        return "PhongThi{" + "id=" + id + ", phongThi='" + phongThi + "', ghiChu='" + ghiChu + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhongThi pt = (PhongThi) o;
        return id == pt.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
