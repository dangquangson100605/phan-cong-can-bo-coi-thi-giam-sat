package common.model;

import java.io.Serializable;

/**
 * Model kết quả phân công giám thị cho 1 phòng thi
 */
public class PhanCong implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int dotId;
    private PhongThi phongThi;
    private CanBo giamThi1;
    private CanBo giamThi2;

    public PhanCong() {}

    public PhanCong(int dotId, PhongThi phongThi, CanBo giamThi1, CanBo giamThi2) {
        this.dotId = dotId;
        this.phongThi = phongThi;
        this.giamThi1 = giamThi1;
        this.giamThi2 = giamThi2;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDotId() { return dotId; }
    public void setDotId(int dotId) { this.dotId = dotId; }

    public PhongThi getPhongThi() { return phongThi; }
    public void setPhongThi(PhongThi phongThi) { this.phongThi = phongThi; }

    public CanBo getGiamThi1() { return giamThi1; }
    public void setGiamThi1(CanBo giamThi1) { this.giamThi1 = giamThi1; }

    public CanBo getGiamThi2() { return giamThi2; }
    public void setGiamThi2(CanBo giamThi2) { this.giamThi2 = giamThi2; }

    @Override
    public String toString() {
        return "PhanCong{" + "phongThi=" + phongThi.getPhongThi() +
               ", GT1=" + giamThi1.getHoTen() +
               ", GT2=" + giamThi2.getHoTen() + "}";
    }
}
