package common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Model đại diện cho Cán bộ coi thi
 */
public class CanBo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String maGV;
    private String hoTen;
    private Date ngaySinh;
    private String donVi;

    public CanBo() {}

    public CanBo(int id, String maGV, String hoTen, Date ngaySinh, String donVi) {
        this.id = id;
        this.maGV = maGV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.donVi = donVi;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMaGV() { return maGV; }
    public void setMaGV(String maGV) { this.maGV = maGV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public Date getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getDonVi() { return donVi; }
    public void setDonVi(String donVi) { this.donVi = donVi; }

    @Override
    public String toString() {
        return "CanBo{" + "id=" + id + ", maGV='" + maGV + "', hoTen='" + hoTen + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CanBo canBo = (CanBo) o;
        return id == canBo.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
