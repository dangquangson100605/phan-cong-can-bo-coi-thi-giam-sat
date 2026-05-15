package common.model;

import java.io.Serializable;
import java.util.List;

/**
 * Wrapper chứa kết quả phân công Server trả về Client.
 * Bao gồm danh sách phân công giám thị và danh sách giám sát hành lang.
 */
public class ResultData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int dotId;
    private List<PhanCong> danhSachPhanCong;
    private List<GiamSat> danhSachGiamSat;

    public ResultData() {}

    public ResultData(int dotId, List<PhanCong> danhSachPhanCong, List<GiamSat> danhSachGiamSat) {
        this.dotId = dotId;
        this.danhSachPhanCong = danhSachPhanCong;
        this.danhSachGiamSat = danhSachGiamSat;
    }

    public int getDotId() { return dotId; }
    public void setDotId(int dotId) { this.dotId = dotId; }

    public List<PhanCong> getDanhSachPhanCong() { return danhSachPhanCong; }
    public void setDanhSachPhanCong(List<PhanCong> danhSachPhanCong) { this.danhSachPhanCong = danhSachPhanCong; }

    public List<GiamSat> getDanhSachGiamSat() { return danhSachGiamSat; }
    public void setDanhSachGiamSat(List<GiamSat> danhSachGiamSat) { this.danhSachGiamSat = danhSachGiamSat; }
}
