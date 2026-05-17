package client.excel;

import common.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Xuat file Excel ket qua phan cong (.xlsx).
 * File 1: Danh sach phan cong coi thi
 * File 2: Danh sach giam sat hanh lang
 */
public class ExcelWriter {

    private static final int MAX_ROWS_PER_SHEET = 20;

    /**
     * Xuat file danh sach phan cong coi thi
     */
    public void exportPhanCong(String filePath, int dotId, List<PhanCong> phanCongList) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle subTitleStyle = createSubTitleStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dataCenterStyle = createDataCenterStyle(workbook);
            CellStyle smallBoldCenterStyle = createSmallBoldCenterStyle(workbook);
            CellStyle quocHieuStyle = createQuocHieuStyle(workbook);

            List<String[]> rows = new ArrayList<>();
            int stt = 1;

            for (PhanCong pc : phanCongList) {
                String phongThi = "";

                if (pc.getPhongThi() != null && pc.getPhongThi().getPhongThi() != null) {
                    phongThi = pc.getPhongThi().getPhongThi();
                }

                if (pc.getGiamThi1() != null) {
                    rows.add(new String[]{
                            formatSTT(stt++),
                            pc.getGiamThi1().getMaGV(),
                            pc.getGiamThi1().getHoTen(),
                            "X",
                            "",
                            phongThi
                    });
                }

                if (pc.getGiamThi2() != null) {
                    rows.add(new String[]{
                            formatSTT(stt++),
                            pc.getGiamThi2().getMaGV(),
                            pc.getGiamThi2().getHoTen(),
                            "",
                            "X",
                            phongThi
                    });
                }
            }

            if (rows.isEmpty()) {
                XSSFSheet sheet = workbook.createSheet("PhanCongCoiThi_1");
                setupSheet(sheet);
                int rowNum = createPhanCongHeader(sheet, dotId, titleStyle, subTitleStyle, centerStyle, headerStyle, smallBoldCenterStyle, quocHieuStyle);
                finishPhanCongSheet(sheet, rowNum, centerStyle);
            } else {
                int sheetIndex = 1;

                for (int start = 0; start < rows.size(); start += MAX_ROWS_PER_SHEET) {
                    XSSFSheet sheet = workbook.createSheet("PhanCongCoiThi_" + sheetIndex++);
                    setupSheet(sheet);

                    int rowNum = createPhanCongHeader(sheet, dotId, titleStyle, subTitleStyle, centerStyle, headerStyle, smallBoldCenterStyle, quocHieuStyle);

                    int end = Math.min(start + MAX_ROWS_PER_SHEET, rows.size());

                    for (int i = start; i < end; i++) {
                        String[] r = rows.get(i);

                        Row dataRow = sheet.createRow(rowNum++);
                        dataRow.setHeightInPoints(25);

                        createStringCell(dataRow, 0, r[0], dataCenterStyle);
                        createStringCell(dataRow, 1, r[1], dataCenterStyle);
                        createStringCell(dataRow, 2, r[2], dataStyle);
                        createStringCell(dataRow, 3, r[3], dataCenterStyle);
                        createStringCell(dataRow, 4, r[4], dataCenterStyle);
                        createStringCell(dataRow, 5, r[5], dataCenterStyle);
                    }

                    finishPhanCongSheet(sheet, rowNum, centerStyle);
                }
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

            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle subTitleStyle = createSubTitleStyle(workbook);
            CellStyle centerStyle = createCenterStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle dataCenterStyle = createDataCenterStyle(workbook);
            CellStyle smallBoldCenterStyle = createSmallBoldCenterStyle(workbook);
            CellStyle quocHieuStyle = createQuocHieuStyle(workbook);

            if (giamSatList.isEmpty()) {
                XSSFSheet sheet = workbook.createSheet("GiamSatHanhLang_1");
                setupSheet(sheet);
                int rowNum = createGiamSatHeader(sheet, dotId, titleStyle, subTitleStyle, centerStyle, headerStyle, smallBoldCenterStyle, quocHieuStyle);
                finishGiamSatSheet(sheet, rowNum, centerStyle);
            } else {
                int sheetIndex = 1;

                for (int start = 0; start < giamSatList.size(); start += MAX_ROWS_PER_SHEET) {
                    XSSFSheet sheet = workbook.createSheet("GiamSatHanhLang_" + sheetIndex++);
                    setupSheet(sheet);

                    int rowNum = createGiamSatHeader(sheet, dotId, titleStyle, subTitleStyle, centerStyle, headerStyle, smallBoldCenterStyle, quocHieuStyle);

                    int end = Math.min(start + MAX_ROWS_PER_SHEET, giamSatList.size());

                    for (int i = start; i < end; i++) {
                        GiamSat gs = giamSatList.get(i);

                        Row dataRow = sheet.createRow(rowNum++);
                        dataRow.setHeightInPoints(25);

                        String maGV = "";
                        String hoTen = "";

                        if (gs.getCanBo() != null) {
                            maGV = gs.getCanBo().getMaGV();
                            hoTen = gs.getCanBo().getHoTen();
                        }

                        String phongGiamSat = buildPhongGiamSat(gs.getTuPhong(), gs.getDenPhong());
                        String diaDiem = gs.getDiaDiem() != null ? gs.getDiaDiem() : "";

                        createStringCell(dataRow, 0, formatSTT(i + 1), dataCenterStyle);
                        createStringCell(dataRow, 1, maGV, dataCenterStyle);
                        createStringCell(dataRow, 2, hoTen, dataStyle);
                        createStringCell(dataRow, 3, phongGiamSat, dataStyle);
                        createStringCell(dataRow, 4, diaDiem, dataStyle);
                    }

                    finishGiamSatSheet(sheet, rowNum, centerStyle);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }
        }

        System.out.println("[ExcelWriter] Da xuat file giam sat: " + filePath);
    }

    // ============================================================
    // Header phan cong coi thi
    // ============================================================

    private int createPhanCongHeader(
            XSSFSheet sheet,
            int dotId,
            CellStyle titleStyle,
            CellStyle subTitleStyle,
            CellStyle centerStyle,
            CellStyle headerStyle,
            CellStyle smallBoldCenterStyle,
            CellStyle quocHieuStyle
    ) {
        int rowNum = 0;

        Row row0 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row0, 0, 2,
                "TRƯỜNG ĐẠI HỌC CÔNG NGHỆ",
                smallBoldCenterStyle);
        createMergedCell(sheet, row0, 3, 5,
                "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                quocHieuStyle);

        Row row1 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row1, 0, 2,
                "HỘI ĐỒNG THI TỐT NGHIỆP",
                smallBoldCenterStyle);
        createMergedCell(sheet, row1, 3, 5,
                "Độc lập - Tự do - Hạnh phúc",
                subTitleStyle);

        rowNum += 2;

        Row row5 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row5, 0, 5,
                "DANH SÁCH PHÂN CÔNG CÁN BỘ COI THI",
                titleStyle);

        Row row6 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row6, 0, 5,
                "Đợt phân công: " + dotId,
                centerStyle);

        rowNum++;

        int tableHeaderStartRow = rowNum;

        Row headerRow1 = sheet.createRow(rowNum++);
        Row headerRow2 = sheet.createRow(rowNum++);

        headerRow1.setHeightInPoints(28);
        headerRow2.setHeightInPoints(32);

        createStringCell(headerRow1, 0, "STT", headerStyle);
        createStringCell(headerRow2, 0, "", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 0, 0));

        createStringCell(headerRow1, 1, "Mã GV", headerStyle);
        createStringCell(headerRow2, 1, "", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 1, 1));

        createStringCell(headerRow1, 2, "Họ và tên", headerStyle);
        createStringCell(headerRow2, 2, "", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 2, 2));

        createStringCell(headerRow1, 3, "GIÁM THỊ", headerStyle);
        createStringCell(headerRow1, 4, "", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow, 3, 4));

        createStringCell(headerRow2, 3, "Giám thị\n1", headerStyle);
        createStringCell(headerRow2, 4, "Giám thị\n2", headerStyle);

        createStringCell(headerRow1, 5, "Phòng thi", headerStyle);
        createStringCell(headerRow2, 5, "", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(tableHeaderStartRow, tableHeaderStartRow + 1, 5, 5));

        sheet.setColumnWidth(0, 8 * 256);
        sheet.setColumnWidth(1, 16 * 256);
        sheet.setColumnWidth(2, 26 * 256);
        sheet.setColumnWidth(3, 16 * 256);
        sheet.setColumnWidth(4, 16 * 256);
        sheet.setColumnWidth(5, 18 * 256);

        return rowNum;
    }

    private void finishPhanCongSheet(XSSFSheet sheet, int rowNum, CellStyle centerStyle) {
        rowNum += 2;

        Row signRow = sheet.createRow(rowNum);
        createMergedCell(sheet, signRow, 3, 5,
                "Người lập danh sách",
                centerStyle);
    }

    // ============================================================
    // Header giam sat hanh lang
    // ============================================================

    private int createGiamSatHeader(
            XSSFSheet sheet,
            int dotId,
            CellStyle titleStyle,
            CellStyle subTitleStyle,
            CellStyle centerStyle,
            CellStyle headerStyle,
            CellStyle smallBoldCenterStyle,
            CellStyle quocHieuStyle
    ) {
        int rowNum = 0;

        Row row0 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row0, 0, 2,
                "TRƯỜNG ĐẠI HỌC CÔNG NGHỆ",
                smallBoldCenterStyle);
        createMergedCell(sheet, row0, 3, 4,
                "CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM",
                quocHieuStyle);

        Row row1 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row1, 0, 2,
                "HỘI ĐỒNG THI TỐT NGHIỆP",
                smallBoldCenterStyle);
        createMergedCell(sheet, row1, 3, 4,
                "Độc lập - Tự do - Hạnh phúc",
                subTitleStyle);

        rowNum += 2;

        Row row5 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row5, 0, 4,
                "DANH SÁCH CÁN BỘ GIÁM SÁT HÀNH LANG",
                titleStyle);

        Row row6 = sheet.createRow(rowNum++);
        createMergedCell(sheet, row6, 0, 4,
                "Đợt phân công: " + dotId,
                centerStyle);

        rowNum++;

        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeightInPoints(36);

        createStringCell(headerRow, 0, "STT", headerStyle);
        createStringCell(headerRow, 1, "Mã GV", headerStyle);
        createStringCell(headerRow, 2, "Họ và tên", headerStyle);
        createStringCell(headerRow, 3, "Phòng thi được giám sát", headerStyle);
        createStringCell(headerRow, 4, "Địa điểm", headerStyle);

        sheet.setColumnWidth(0, 10 * 256);
        sheet.setColumnWidth(1, 16 * 256);
        sheet.setColumnWidth(2, 26 * 256);
        sheet.setColumnWidth(3, 38 * 256);
        sheet.setColumnWidth(4, 22 * 256);

        return rowNum;
    }

    private void finishGiamSatSheet(XSSFSheet sheet, int rowNum, CellStyle centerStyle) {
        rowNum += 2;

        Row signRow = sheet.createRow(rowNum);
        createMergedCell(sheet, signRow, 3, 4,
                "Người lập danh sách",
                centerStyle);
    }

    // ============================================================
    // Helper methods
    // ============================================================

    private void setupSheet(XSSFSheet sheet) {
        sheet.setDisplayGridlines(false);

        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        printSetup.setLandscape(false);
        printSetup.setFitWidth((short) 1);
        printSetup.setFitHeight((short) 0);

        sheet.setFitToPage(true);

        sheet.setMargin(Sheet.TopMargin, 0.4);
        sheet.setMargin(Sheet.BottomMargin, 0.4);
        sheet.setMargin(Sheet.LeftMargin, 0.4);
        sheet.setMargin(Sheet.RightMargin, 0.4);
    }

    private String formatSTT(int stt) {
        return String.format("%02d", stt);
    }

    private String buildPhongGiamSat(String tuPhong, String denPhong) {
        if (tuPhong == null) {
            tuPhong = "";
        }

        if (denPhong == null) {
            denPhong = "";
        }

        if (!tuPhong.isEmpty() && !denPhong.isEmpty()) {
            return "Từ " + tuPhong + " đến " + denPhong;
        }

        if (!tuPhong.isEmpty()) {
            return "Từ " + tuPhong;
        }

        if (!denPhong.isEmpty()) {
            return "Đến " + denPhong;
        }

        return "";
    }

    private void createMergedCell(XSSFSheet sheet, Row row, int firstCol, int lastCol, String value, CellStyle style) {
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        for (int col = firstCol + 1; col <= lastCol; col++) {
            Cell emptyCell = row.createCell(col);
            emptyCell.setCellStyle(style);
        }

        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), firstCol, lastCol));
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
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private CellStyle createSubTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setItalic(true);
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createDataCenterStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createCenterStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private CellStyle createSmallBoldCenterStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private CellStyle createQuocHieuStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();

        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        font.setFontName("Times New Roman");

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }
}