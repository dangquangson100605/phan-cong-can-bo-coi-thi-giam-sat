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

        // Luu can bo va phong thi vao DB, lay lai danh sach voi id thuc te tu DB
        List<CanBo> savedCanBo = dbManager.saveCanBoList(canBoList);
        List<PhongThi> savedPhongThi = dbManager.savePhongThiList(phongThiList);

        // Lay lich su phan cong tu DB
        Map<Integer, Set<Integer>> historyRooms = dbManager.getHistoryRooms();
        Set<String> historyPairs = dbManager.getHistoryPairs();

        // Tao dot phan cong moi
        int dotId = dbManager.createDotPhanCong(m, n);

        // === THUAT TOAN PHAN CONG ===
        List<PhanCong> phanCongList = new ArrayList<>();
        List<CanBo> available = new ArrayList<>(savedCanBo);

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
     * Phan cong giam sat hanh lang: nhom phong thi theo dia diem (ghiChu),
     * phan bo can bo giam sat theo ty le cho tung nhom dia diem,
     * dam bao khong co can bo giam sat phong o nhieu dia diem khac nhau.
     */
    private List<GiamSat> phanCongGiamSat(int dotId, List<CanBo> giamSatCanBo, List<PhongThi> phongThiList) {
        List<GiamSat> result = new ArrayList<>();

        if (giamSatCanBo.isEmpty() || phongThiList.isEmpty()) {
            return result;
        }

        // Buoc 1: Nhom phong thi theo dia diem (ghiChu), giu nguyen thu tu
        LinkedHashMap<String, List<PhongThi>> locationGroups = new LinkedHashMap<>();
        for (PhongThi pt : phongThiList) {
            String location = (pt.getGhiChu() != null && !pt.getGhiChu().trim().isEmpty())
                    ? pt.getGhiChu().trim()
                    : "Khác";
            locationGroups.computeIfAbsent(location, k -> new ArrayList<>()).add(pt);
        }

        int totalRooms = phongThiList.size();
        int supervisorCount = giamSatCanBo.size();

        // Buoc 2: Phan bo so luong giam sat cho tung nhom dia diem theo ty le
        List<String> locationKeys = new ArrayList<>(locationGroups.keySet());
        int[] supervisorsPerLocation = new int[locationKeys.size()];
        int assignedSupervisors = 0;

        for (int i = 0; i < locationKeys.size(); i++) {
            int groupSize = locationGroups.get(locationKeys.get(i)).size();
            // Phan bo ty le, lam tron xuong
            supervisorsPerLocation[i] = (int) Math.floor((double) groupSize / totalRooms * supervisorCount);
            // Dam bao moi nhom co it nhat 1 giam sat
            if (supervisorsPerLocation[i] < 1) {
                supervisorsPerLocation[i] = 1;
            }
            assignedSupervisors += supervisorsPerLocation[i];
        }

        // Phan bo giam sat con du cho nhom co nhieu phong nhat
        int remaining = supervisorCount - assignedSupervisors;
        if (remaining > 0) {
            // Sap xep index theo so phong giam dan de uu tien nhom lon
            List<Integer> sortedIndices = new ArrayList<>();
            for (int i = 0; i < locationKeys.size(); i++) sortedIndices.add(i);
            sortedIndices.sort((a, b) -> locationGroups.get(locationKeys.get(b)).size()
                    - locationGroups.get(locationKeys.get(a)).size());

            for (int idx = 0; remaining > 0; idx = (idx + 1) % sortedIndices.size()) {
                supervisorsPerLocation[sortedIndices.get(idx)]++;
                remaining--;
            }
        } else if (remaining < 0) {
            // Neu tong vuot qua supervisorCount (do dam bao min 1), cat bot tu nhom nho nhat
            List<Integer> sortedIndices = new ArrayList<>();
            for (int i = 0; i < locationKeys.size(); i++) sortedIndices.add(i);
            sortedIndices.sort((a, b) -> locationGroups.get(locationKeys.get(a)).size()
                    - locationGroups.get(locationKeys.get(b)).size());

            boolean canDecrease = true;
            for (int idx = 0; remaining < 0 && canDecrease; idx = (idx + 1) % sortedIndices.size()) {
                canDecrease = false;
                for (int count : supervisorsPerLocation) {
                    if (count > 0) { // Allow reducing to 0 if we really don't have enough supervisors
                        canDecrease = true;
                        break;
                    }
                }
                
                if (!canDecrease) break;

                if (supervisorsPerLocation[sortedIndices.get(idx)] > 0) {
                    supervisorsPerLocation[sortedIndices.get(idx)]--;
                    remaining++;
                }
            }
        }

        // Buoc 3: Phan cong giam sat trong tung nhom dia diem
        int supervisorIndex = 0;
        for (int g = 0; g < locationKeys.size(); g++) {
            List<PhongThi> groupRooms = locationGroups.get(locationKeys.get(g));
            int groupSupervisorCount = supervisorsPerLocation[g];

            // Dam bao khong vuot qua so luong giam sat con lai
            if (supervisorIndex + groupSupervisorCount > supervisorCount) {
                groupSupervisorCount = supervisorCount - supervisorIndex;
            }
            if (groupSupervisorCount <= 0) continue;

            int groupRoomCount = groupRooms.size();
            int roomsPerSupervisor = groupRoomCount / groupSupervisorCount;
            int groupRemainder = groupRoomCount % groupSupervisorCount;

            int currentRoom = 0;
            for (int i = 0; i < groupSupervisorCount && supervisorIndex < supervisorCount; i++) {
                int roomCount = roomsPerSupervisor + (i < groupRemainder ? 1 : 0);
                if (roomCount == 0) roomCount = 1;

                int startRoom = currentRoom;
                int endRoom = Math.min(currentRoom + roomCount - 1, groupRoomCount - 1);

                String tuPhong = groupRooms.get(startRoom).getPhongThi();
                String denPhong = groupRooms.get(endRoom).getPhongThi();

                GiamSat gs = new GiamSat(dotId, giamSatCanBo.get(supervisorIndex), tuPhong, denPhong, locationKeys.get(g));
                result.add(gs);

                supervisorIndex++;
                currentRoom = endRoom + 1;
                if (currentRoom >= groupRoomCount) break;
            }
        }

        // Neu van con giam sat chua duoc phan cong (truong hop hiem), phan cong tat ca phong
        for (int i = supervisorIndex; i < supervisorCount; i++) {
            GiamSat gsExtra = new GiamSat(dotId, giamSatCanBo.get(i),
                    phongThiList.get(0).getPhongThi(),
                    phongThiList.get(phongThiList.size() - 1).getPhongThi(), "");
            result.add(gsExtra);
        }

        return result;
    }
}
