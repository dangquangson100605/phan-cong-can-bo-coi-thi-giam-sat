package common.model;

import java.io.Serializable;
import java.util.List;

/**
 * Wrapper chứa dữ liệu Client gửi lên Server để yêu cầu phân công.
 * Bao gồm danh sách cán bộ và danh sách phòng thi.
 */
public class RequestData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CanBo> danhSachCanBo;
    private List<PhongThi> danhSachPhongThi;

    public RequestData() {}

    public RequestData(List<CanBo> danhSachCanBo, List<PhongThi> danhSachPhongThi) {
        this.danhSachCanBo = danhSachCanBo;
        this.danhSachPhongThi = danhSachPhongThi;
    }

    public List<CanBo> getDanhSachCanBo() { return danhSachCanBo; }
    public void setDanhSachCanBo(List<CanBo> danhSachCanBo) { this.danhSachCanBo = danhSachCanBo; }

    public List<PhongThi> getDanhSachPhongThi() { return danhSachPhongThi; }
    public void setDanhSachPhongThi(List<PhongThi> danhSachPhongThi) { this.danhSachPhongThi = danhSachPhongThi; }
}
