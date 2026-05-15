package server.service;

import common.model.*;
import server.db.DatabaseManager;

import java.sql.SQLException;
import java.util.*;

/**
 * Service thuc hien thuat toan phan cong giam thi.
 * Su dung Random khong lap, khong de quy.
 */
public class AssignmentService {

    private final DatabaseManager dbManager;

    public AssignmentService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Thuc hien phan cong giam thi va giam sat hanh lang.
     *
     * @param canBoList    Danh sach can bo
     * @param phongThiList Danh sach phong thi (da co id tu DB)
     * @return ResultData chua ket qua phan cong
     */
    public ResultData phanCong(List<CanBo> canBoList, List<PhongThi> phongThiList) throws Exception {
        int n = canBoList.size();
        int m = phongThiList.size();

        // Kiem tra dieu kien du lieu
        if (n < 2 * m) {
            throw new Exception("Khong du can bo coi thi! Can it nhat " + (2 * m) +
                    " can bo cho " + m + " phong thi, hien chi co " + n + " can bo. Vui long nhap lai du lieu.");
        }

        // Luu can bo va phong thi vao DB
        dbManager.saveCanBoList(canBoList);
        List<PhongThi> savedPhongThi = dbManager.savePhongThiList(phongThiList);

        // Lay lich su phan cong tu DB
        Map<Integer, Set<Integer>> historyRooms = dbManager.getHistoryRooms();
        Set<String> historyPairs = dbManager.getHistoryPairs();

        // Tao dot phan cong moi
        int dotId = dbManager.createDotPhanCong(m, n);

        // === THUAT TOAN PHAN CONG ===
        List<PhanCong> phanCongList = new ArrayList<>();
        List<CanBo> available = new ArrayList<>(canBoList);

        // Buoc 1: Shuffle ngau nhien
        Collections.shuffle(available, new Random());

        // Danh sach can bo da duoc phan cong lam giam thi
        Set<Integer> assigned = new HashSet<>();

        for (int i = 0; i < savedPhongThi.size(); i++) {
            PhongThi phong = savedPhongThi.get(i);

            // Chon Giam thi 1
            CanBo gt1 = null;
            int gt1Index = -1;
            for (int j = 0; j < available.size(); j++) {
                CanBo candidate = available.get(j);
                if (!assigned.contains(candidate.getId()) && !daCoi(historyRooms, candidate.getId(), phong.getId())) {
                    gt1 = candidate;
                    gt1Index = j;
                    break;
                }
            }

            // Neu khong tim duoc GT1 (do rang buoc phong), thu bo qua rang buoc phong
            if (gt1 == null) {
                for (int j = 0; j < available.size(); j++) {
                    if (!assigned.contains(available.get(j).getId())) {
                        gt1 = available.get(j);
                        gt1Index = j;
                        break;
                    }
                }
            }

            if (gt1 == null) {
                throw new Exception("Khong the phan cong giam thi 1 cho phong " + phong.getPhongThi());
            }

            assigned.add(gt1.getId());

            // Chon Giam thi 2
            CanBo gt2 = null;
            int gt2Index = -1;
            for (int j = 0; j < available.size(); j++) {
                CanBo candidate = available.get(j);
                if (!assigned.contains(candidate.getId())
                        && !daCoi(historyRooms, candidate.getId(), phong.getId())
                        && !daGhepCap(historyPairs, gt1.getId(), candidate.getId())) {
                    gt2 = candidate;
                    gt2Index = j;
                    break;
                }
            }

            // Neu khong tim duoc GT2 thoa man ca 2 rang buoc, noi long rang buoc cap
            if (gt2 == null) {
                for (int j = 0; j < available.size(); j++) {
                    CanBo candidate = available.get(j);
                    if (!assigned.contains(candidate.getId())
                            && !daCoi(historyRooms, candidate.getId(), phong.getId())) {
                        gt2 = candidate;
                        gt2Index = j;
                        break;
                    }
                }
            }

            // Noi long tiep: bo ca rang buoc phong
            if (gt2 == null) {
                for (int j = 0; j < available.size(); j++) {
                    CanBo candidate = available.get(j);
                    if (!assigned.contains(candidate.getId())) {
                        gt2 = candidate;
                        gt2Index = j;
                        break;
                    }
                }
            }

            if (gt2 == null) {
                throw new Exception("Khong the phan cong giam thi 2 cho phong " + phong.getPhongThi());
            }

            assigned.add(gt2.getId());

            PhanCong pc = new PhanCong(dotId, phong, gt1, gt2);
            phanCongList.add(pc);
        }

        // === PHAN CONG GIAM SAT HANH LANG ===
        List<CanBo> giamSatCanBo = new ArrayList<>();
        for (CanBo cb : available) {
            if (!assigned.contains(cb.getId())) {
                giamSatCanBo.add(cb);
            }
        }

        List<GiamSat> giamSatList = phanCongGiamSat(dotId, giamSatCanBo, savedPhongThi);

        // Luu ket qua vao DB
        dbManager.savePhanCong(dotId, phanCongList);
        dbManager.saveGiamSat(dotId, giamSatList);

        System.out.println("[Service] Phan cong thanh cong dot " + dotId +
                ": " + phanCongList.size() + " phong, " + giamSatList.size() + " giam sat hanh lang.");

        return new ResultData(dotId, phanCongList, giamSatList);
    }

    /**
     * Kiem tra can bo da coi phong nay chua (lich su)
     */
    private boolean daCoi(Map<Integer, Set<Integer>> historyRooms, int canBoId, int phongThiId) {
        Set<Integer> rooms = historyRooms.get(canBoId);
        return rooms != null && rooms.contains(phongThiId);
    }

    /**
     * Kiem tra 2 giam thi da ghep cap chua (lich su)
     */
    private boolean daGhepCap(Set<String> historyPairs, int id1, int id2) {
        String pairKey = Math.min(id1, id2) + "-" + Math.max(id1, id2);
        return historyPairs.contains(pairKey);
    }

    /**
     * Phan cong giam sat hanh lang: chia deu phong thi cho cac can bo con lai
     */
    private List<GiamSat> phanCongGiamSat(int dotId, List<CanBo> giamSatCanBo, List<PhongThi> phongThiList) {
        List<GiamSat> result = new ArrayList<>();

        if (giamSatCanBo.isEmpty()) {
            return result;
        }

        int totalRooms = phongThiList.size();
        int supervisorCount = giamSatCanBo.size();
        int roomsPerSupervisor = totalRooms / supervisorCount;
        int remainder = totalRooms % supervisorCount;

        int currentRoom = 0;
        for (int i = 0; i < supervisorCount; i++) {
            int roomCount = roomsPerSupervisor + (i < remainder ? 1 : 0);
            if (roomCount == 0) roomCount = 1;

            int startRoom = currentRoom;
            int endRoom = Math.min(currentRoom + roomCount - 1, totalRooms - 1);

            String tuPhong = phongThiList.get(startRoom).getPhongThi();
            String denPhong = phongThiList.get(endRoom).getPhongThi();

            GiamSat gs = new GiamSat(dotId, giamSatCanBo.get(i), tuPhong, denPhong);
            result.add(gs);

            currentRoom = endRoom + 1;
            if (currentRoom >= totalRooms) {
                // Cac can bo con lai giam sat tat ca phong
                for (int j = i + 1; j < supervisorCount; j++) {
                    GiamSat gsExtra = new GiamSat(dotId, giamSatCanBo.get(j),
                            phongThiList.get(0).getPhongThi(),
                            phongThiList.get(totalRooms - 1).getPhongThi());
                    result.add(gsExtra);
                }
                break;
            }
        }

        return result;
    }
}
