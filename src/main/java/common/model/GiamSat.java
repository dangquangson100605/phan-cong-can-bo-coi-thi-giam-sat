package common.model;

import java.io.Serializable;

/**
 * Model đại diện cho Cán bộ giám sát hành lang
 */
public class GiamSat implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int dotId;
    private CanBo canBo;
    private String tuPhong;
    private String denPhong;
    private String diaDiem;

    public GiamSat() {}

    public GiamSat(int dotId, CanBo canBo, String tuPhong, String denPhong) {
        this.dotId = dotId;
        this.canBo = canBo;
        this.tuPhong = tuPhong;
        this.denPhong = denPhong;
    }

    public GiamSat(int dotId, CanBo canBo, String tuPhong, String denPhong, String diaDiem) {
        this.dotId = dotId;
        this.canBo = canBo;
        this.tuPhong = tuPhong;
        this.denPhong = denPhong;
        this.diaDiem = diaDiem;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDotId() { return dotId; }
    public void setDotId(int dotId) { this.dotId = dotId; }

    public CanBo getCanBo() { return canBo; }
    public void setCanBo(CanBo canBo) { this.canBo = canBo; }

    public String getTuPhong() { return tuPhong; }
    public void setTuPhong(String tuPhong) { this.tuPhong = tuPhong; }

    public String getDenPhong() { return denPhong; }
    public void setDenPhong(String denPhong) { this.denPhong = denPhong; }

    public String getDiaDiem() { return diaDiem; }
    public void setDiaDiem(String diaDiem) { this.diaDiem = diaDiem; }

    @Override
    public String toString() {
        return "GiamSat{" + "canBo=" + canBo.getHoTen() +
               ", tuPhong='" + tuPhong + "', denPhong='" + denPhong +
               "', diaDiem='" + diaDiem + "'}";
    }
}
