package client.excel;

import common.model.CanBo;
import common.model.PhongThi;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Doc file Excel dau vao (.xlsx).
 * Sheet 1: Danh sach can bo (ID, Ma GV, Ho Ten, Ngay Sinh, Don vi)
 * Sheet 2: Danh sach phong thi (STT, Phong Thi, Ghi Chu)
 */
public class ExcelReader {

    /**
     * Doc danh sach can bo tu Sheet 1
     */
    public List<CanBo> readCanBoList(String filePath) throws IOException {
        List<CanBo> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IOException("Khong tim thay Sheet 1 (Danh sach can bo)");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bo dong tieu de
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    int id = (int) getNumericValue(row.getCell(0));
                    String maGV = getStringValue(row.getCell(1));
                    String hoTen = getStringValue(row.getCell(2));
                    Date ngaySinh = getDateValue(row.getCell(3));
                    String donVi = getStringValue(row.getCell(4));

                    if (maGV != null && !maGV.isEmpty() && hoTen != null && !hoTen.isEmpty()) {
                        CanBo cb = new CanBo(id, maGV, hoTen, ngaySinh, donVi);
                        result.add(cb);
                    }
                } catch (Exception e) {
                    System.err.println("[ExcelReader] Loi doc dong " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        System.out.println("[ExcelReader] Da doc " + result.size() + " can bo.");
        return result;
    }

    /**
     * Doc danh sach phong thi tu Sheet 2
     */
    public List<PhongThi> readPhongThiList(String filePath) throws IOException {
        List<PhongThi> result = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(1);
            if (sheet == null) {
                throw new IOException("Khong tim thay Sheet 2 (Danh sach phong thi)");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    int stt = (int) getNumericValue(row.getCell(0));
                    String phongThi = getStringValue(row.getCell(1));
                    String ghiChu = getStringValue(row.getCell(2));

                    if (phongThi != null && !phongThi.isEmpty()) {
                        PhongThi pt = new PhongThi(0, phongThi, ghiChu);
                        result.add(pt);
                    }
                } catch (Exception e) {
                    System.err.println("[ExcelReader] Loi doc dong " + (i + 1) + ": " + e.getMessage());
                }
            }
        }

        System.out.println("[ExcelReader] Da doc " + result.size() + " phong thi.");
        return result;
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                double val = cell.getNumericCellValue();
                if (val == Math.floor(val)) {
                    return String.valueOf((int) val);
                }
                return String.valueOf(val);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private double getNumericValue(Cell cell) {
        if (cell == null) return 0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    private Date getDateValue(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
