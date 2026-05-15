package util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Tao file Excel mau de test he thong.
 * Chay: java util.SampleDataGenerator
 */
public class SampleDataGenerator {

    public static void main(String[] args) throws IOException {
        String filePath = "sample/DuLieuMau.xlsx";
        generateSampleData(filePath);
        System.out.println("Da tao file mau: " + filePath);
    }

    public static void generateSampleData(String filePath) throws IOException {
        new java.io.File("sample").mkdirs();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // === SHEET 1: Danh sach can bo ===
            XSSFSheet sheet1 = workbook.createSheet("DanhSachCanBo");

            // Header
            Row header1 = sheet1.createRow(0);
            String[] h1 = {"ID", "Ma GV", "Ho Ten", "Ngay Sinh", "Don vi cong tac"};
            CellStyle headerStyle = createHeaderStyle(workbook);
            for (int i = 0; i < h1.length; i++) {
                Cell cell = header1.createCell(i);
                cell.setCellValue(h1[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data - 20 can bo mau
            String[][] canBoData = {
                {"1", "GV001", "Nguyen Van An", "", "Khoa Cong nghe Thong tin"},
                {"2", "GV002", "Tran Thi Binh", "", "Khoa Cong nghe Thong tin"},
                {"3", "GV003", "Le Van Cuong", "", "Khoa Dien - Dien tu"},
                {"4", "GV004", "Pham Thi Dung", "", "Khoa Dien - Dien tu"},
                {"5", "GV005", "Hoang Van Em", "", "Khoa Co khi"},
                {"6", "GV006", "Vo Thi Phuong", "", "Khoa Co khi"},
                {"7", "GV007", "Bui Van Giang", "", "Khoa Kinh te"},
                {"8", "GV008", "Dang Thi Hanh", "", "Khoa Kinh te"},
                {"9", "GV009", "Ngo Van Ich", "", "Khoa Ngoai ngu"},
                {"10", "GV010", "Ly Thi Kim", "", "Khoa Ngoai ngu"},
                {"11", "GV011", "Duong Van Lam", "", "Khoa Xay dung"},
                {"12", "GV012", "Mai Thi Ngoc", "", "Khoa Xay dung"},
                {"13", "GV013", "Truong Van Oanh", "", "Khoa Moi truong"},
                {"14", "GV014", "Dinh Thi Phuong", "", "Khoa Moi truong"},
                {"15", "GV015", "Ha Van Quang", "", "Khoa Hoa hoc"},
                {"16", "GV016", "Cao Thi Rang", "", "Khoa Hoa hoc"},
                {"17", "GV017", "Luong Van Son", "", "Khoa Toan"},
                {"18", "GV018", "Nguyen Thi Thuy", "", "Khoa Toan"},
                {"19", "GV019", "Tran Van Uyen", "", "Khoa Vat ly"},
                {"20", "GV020", "Le Thi Van", "", "Khoa Vat ly"}
            };

            for (int i = 0; i < canBoData.length; i++) {
                Row row = sheet1.createRow(i + 1);
                for (int j = 0; j < canBoData[i].length; j++) {
                    Cell cell = row.createCell(j);
                    if (j == 0) {
                        cell.setCellValue(Integer.parseInt(canBoData[i][j]));
                    } else {
                        cell.setCellValue(canBoData[i][j]);
                    }
                }
            }

            for (int i = 0; i < h1.length; i++) sheet1.autoSizeColumn(i);

            // === SHEET 2: Danh sach phong thi ===
            XSSFSheet sheet2 = workbook.createSheet("DanhSachPhongThi");

            Row header2 = sheet2.createRow(0);
            String[] h2 = {"STT", "Phong Thi", "Ghi Chu"};
            for (int i = 0; i < h2.length; i++) {
                Cell cell = header2.createCell(i);
                cell.setCellValue(h2[i]);
                cell.setCellStyle(headerStyle);
            }

            String[][] phongThiData = {
                {"1", "A101", "Co so 1 - TP.HCM"},
                {"2", "A102", "Co so 1 - TP.HCM"},
                {"3", "A201", "Co so 1 - TP.HCM"},
                {"4", "A202", "Co so 1 - TP.HCM"},
                {"5", "B101", "Co so 2 - Thu Duc"},
                {"6", "B102", "Co so 2 - Thu Duc"},
                {"7", "B201", "Co so 2 - Thu Duc"},
                {"8", "B202", "Co so 2 - Thu Duc"}
            };

            for (int i = 0; i < phongThiData.length; i++) {
                Row row = sheet2.createRow(i + 1);
                for (int j = 0; j < phongThiData[i].length; j++) {
                    Cell cell = row.createCell(j);
                    if (j == 0) {
                        cell.setCellValue(Integer.parseInt(phongThiData[i][j]));
                    } else {
                        cell.setCellValue(phongThiData[i][j]);
                    }
                }
            }

            for (int i = 0; i < h2.length; i++) sheet2.autoSizeColumn(i);

            // Write file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
    }

    private static CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
