package client.excel;

import common.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Xuat file Excel ket qua phan cong (.xlsx).
 * File 1: Danh sach phan cong coi thi
 * File 2: Danh sach giam sat hanh lang
 */
public class ExcelWriter {

    /**
     * Xuat file danh sach phan cong coi thi
     */
    public void exportPhanCong(String filePath, int dotId, List<PhanCong> phanCongList) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("PhanCongCoiThi");
            sheet.setDefaultColumnWidth(18);

            // Styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle subTitleStyle = createSubTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);

            int rowNum = 0;

            // Quoc hieu
            Row row0 = sheet.createRow(rowNum++);
            Cell cell0 = row0.createCell(0);
            cell0.setCellValue("CONG HOA XA HOI CHU NGHIA VIET NAM");
            cell0.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

            Row row1 = sheet.createRow(rowNum++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("Doc lap - Tu do - Hanh phuc");
            cell1.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            rowNum++; // Dong trong

            Row row3 = sheet.createRow(rowNum++);
            Cell cell3 = row3.createCell(0);
            cell3.setCellValue("TRUONG DAI HOC CONG NGHE");
            cell3.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 7));

            rowNum++; // Dong trong

            Row row5 = sheet.createRow(rowNum++);
            Cell cell5 = row5.createCell(0);
            cell5.setCellValue("DANH SACH PHAN CONG CAN BO COI THI");
            cell5.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 7));

            Row row6 = sheet.createRow(rowNum++);
            Cell cell6 = row6.createCell(0);
            cell6.setCellValue("Dot phan cong: " + dotId);
            cell6.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 7));

            rowNum++; // Dong trong

            // Header bang
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"STT", "Lan PC", "Phong Thi", "Dia diem", "Ma GV GT1", "Ho ten GT1", "Ma GV GT2", "Ho ten GT2"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            for (int i = 0; i < phanCongList.size(); i++) {
                PhanCong pc = phanCongList.get(i);
                Row dataRow = sheet.createRow(rowNum++);

                createCell(dataRow, 0, i + 1, dataStyle);
                createCell(dataRow, 1, dotId, dataStyle);
                createStringCell(dataRow, 2, pc.getPhongThi().getPhongThi(), dataStyle);
                createStringCell(dataRow, 3, pc.getPhongThi().getGhiChu() != null ? pc.getPhongThi().getGhiChu() : "", dataStyle);
                createStringCell(dataRow, 4, pc.getGiamThi1().getMaGV(), dataStyle);
                createStringCell(dataRow, 5, pc.getGiamThi1().getHoTen(), dataStyle);
                createStringCell(dataRow, 6, pc.getGiamThi2().getMaGV(), dataStyle);
                createStringCell(dataRow, 7, pc.getGiamThi2().getHoTen(), dataStyle);
            }

            rowNum += 2;
            Row signRow = sheet.createRow(rowNum);
            Cell signCell = signRow.createCell(5);
            signCell.setCellValue("Nguoi lap danh sach");
            signCell.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 5, 7));

            // Auto-size columns
            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
        System.out.println("[ExcelWriter] Da xuat file phan cong: " + filePath);
    }

    /**
     * Xuat file danh sach giam sat hanh lang
     */
    public void exportGiamSat(String filePath, int dotId, List<GiamSat> giamSatList) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("GiamSatHanhLang");
            sheet.setDefaultColumnWidth(18);

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle subTitleStyle = createSubTitleStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);

            int rowNum = 0;

            // Quoc hieu
            Row row0 = sheet.createRow(rowNum++);
            Cell cell0 = row0.createCell(0);
            cell0.setCellValue("CONG HOA XA HOI CHU NGHIA VIET NAM");
            cell0.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            Row row1 = sheet.createRow(rowNum++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("Doc lap - Tu do - Hanh phuc");
            cell1.setCellStyle(subTitleStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 6));

            rowNum++;

            Row row3 = sheet.createRow(rowNum++);
            Cell cell3 = row3.createCell(0);
            cell3.setCellValue("TRUONG DAI HOC CONG NGHE");
            cell3.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 6));

            rowNum++;

            Row row5 = sheet.createRow(rowNum++);
            Cell cell5 = row5.createCell(0);
            cell5.setCellValue("DANH SACH CAN BO GIAM SAT HANH LANG");
            cell5.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 6));

            Row row6 = sheet.createRow(rowNum++);
            Cell cell6 = row6.createCell(0);
            cell6.setCellValue("Dot phan cong: " + dotId);
            cell6.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 6));

            rowNum++;

            // Header bang
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"STT", "Lan PC", "Ma GV", "Ho ten", "Don vi cong tac", "Tu phong", "Den phong"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data
            for (int i = 0; i < giamSatList.size(); i++) {
                GiamSat gs = giamSatList.get(i);
                Row dataRow = sheet.createRow(rowNum++);

                createCell(dataRow, 0, i + 1, dataStyle);
                createCell(dataRow, 1, dotId, dataStyle);
                createStringCell(dataRow, 2, gs.getCanBo().getMaGV(), dataStyle);
                createStringCell(dataRow, 3, gs.getCanBo().getHoTen(), dataStyle);
                createStringCell(dataRow, 4, gs.getCanBo().getDonVi() != null ? gs.getCanBo().getDonVi() : "", dataStyle);
                createStringCell(dataRow, 5, gs.getTuPhong(), dataStyle);
                createStringCell(dataRow, 6, gs.getDenPhong(), dataStyle);
            }

            rowNum += 2;
            Row signRow = sheet.createRow(rowNum);
            Cell signCell = signRow.createCell(4);
            signCell.setCellValue("Nguoi lap danh sach");
            signCell.setCellStyle(centerStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 4, 6));

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }
        System.out.println("[ExcelWriter] Da xuat file giam sat: " + filePath);
    }

    // === Helper methods ===

    private void createCell(Row row, int col, int value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createStringCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }

    private CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createSubTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setItalic(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createCenterStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
