package client.ui;

import client.ClientConnection;
import client.excel.ExcelReader;
import client.excel.ExcelWriter;
import common.model.*;
import common.protocol.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

/**
 * Giao dien chinh cua Client - Java Swing.
 */
public class MainFrame extends JFrame {

    private final ClientConnection connection;
    private JTextField hostField, portField;
    private JTextField nField, mField; // n = so can bo, m = so phong thi (nhap tay)
    private JLabel nMaxLabel, mMaxLabel; // Hien thi so toi da tu file Excel
    private JButton connectBtn, disconnectBtn;
    private JLabel statusLabel;
    private JLabel fileLabel;
    private JButton chooseFileBtn, sendBtn, exportPhanCongBtn, exportGiamSatBtn;
    private JTable canBoTable, phongThiTable, phanCongTable, giamSatTable;
    private JTabbedPane inputTabs, resultTabs;

    private String selectedFilePath = null;
    private List<CanBo> canBoList = null;
    private List<PhongThi> phongThiList = null;
    private ResultData lastResult = null;

    public MainFrame() {
        connection = new ClientConnection();
        initUI();
    }

    private void initUI() {
        setTitle("HE THONG PHAN CONG CAN BO COI THI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG_DARK);
        setLayout(new BorderLayout(0, 0));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        // Main content
        add(createMainPanel(), BorderLayout.CENTER);
        // Status bar
        add(createStatusBar(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // === HEADER ===
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(25, 28, 35));
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // Title
        JLabel title = UIHelper.createLabel(
                "\u2605  HE THONG PHAN CONG CAN BO COI THI",
                UIHelper.FONT_TITLE, UIHelper.PRIMARY_LIGHT);
        header.add(title, BorderLayout.WEST);

        // Connection panel
        JPanel connPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        connPanel.setOpaque(false);

        connPanel.add(UIHelper.createLabel("Host:", UIHelper.FONT_BODY, UIHelper.TEXT_SECONDARY));
        hostField = UIHelper.createTextField(10);
        hostField.setText("localhost");
        connPanel.add(hostField);

        connPanel.add(UIHelper.createLabel("Port:", UIHelper.FONT_BODY, UIHelper.TEXT_SECONDARY));
        portField = UIHelper.createTextField(5);
        portField.setText("9999");
        connPanel.add(portField);

        connectBtn = UIHelper.createButton("Ket noi", UIHelper.SUCCESS);
        connectBtn.setPreferredSize(new Dimension(110, 34));
        connectBtn.addActionListener(e -> doConnect());
        connPanel.add(connectBtn);

        disconnectBtn = UIHelper.createButton("Ngat", UIHelper.ERROR);
        disconnectBtn.setPreferredSize(new Dimension(80, 34));
        disconnectBtn.setEnabled(false);
        disconnectBtn.addActionListener(e -> doDisconnect());
        connPanel.add(disconnectBtn);

        header.add(connPanel, BorderLayout.EAST);
        return header;
    }

    // === MAIN PANEL ===
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(UIHelper.BG_DARK);
        main.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Left: Input section
        JPanel leftPanel = createInputSection();
        leftPanel.setPreferredSize(new Dimension(500, 0));

        // Right: Result section
        JPanel rightPanel = createResultSection();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setBackground(UIHelper.BG_DARK);

        main.add(splitPane, BorderLayout.CENTER);
        return main;
    }

    // === INPUT SECTION ===
    private JPanel createInputSection() {
        JPanel panel = UIHelper.createTitledPanel("DU LIEU DAU VAO");
        panel.setLayout(new BorderLayout(0, 10));

        // Top area: file chooser + m/n input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // File chooser area
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filePanel.setOpaque(false);

        chooseFileBtn = UIHelper.createButton("[+] Chon file Excel", UIHelper.PRIMARY);
        chooseFileBtn.addActionListener(e -> doChooseFile());
        filePanel.add(chooseFileBtn);

        fileLabel = UIHelper.createLabel("Chua chon file", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        filePanel.add(fileLabel);

        topPanel.add(filePanel);

        // m, n input area
        JPanel mnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        mnPanel.setOpaque(false);

        mnPanel.add(UIHelper.createLabel("n (So can bo):", UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
        nField = UIHelper.createTextField(6);
        nField.setToolTipText("Nhap so luong can bo coi thi (n)");
        mnPanel.add(nField);
        nMaxLabel = UIHelper.createLabel("", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        mnPanel.add(nMaxLabel);

        mnPanel.add(Box.createHorizontalStrut(15));

        mnPanel.add(UIHelper.createLabel("m (So phong thi):", UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
        mField = UIHelper.createTextField(6);
        mField.setToolTipText("Nhap so luong phong thi (m)");
        mnPanel.add(mField);
        mMaxLabel = UIHelper.createLabel("", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        mnPanel.add(mMaxLabel);

        topPanel.add(mnPanel);

        // Dieu kien label
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        condPanel.setOpaque(false);
        condPanel.add(UIHelper.createLabel("\u26A0 Dieu kien: n \u2265 2m (so can bo phai gap doi so phong thi)", UIHelper.FONT_SMALL, UIHelper.WARNING));
        topPanel.add(condPanel);

        panel.add(topPanel, BorderLayout.NORTH);

        // Input data preview tabs
        inputTabs = new JTabbedPane();
        inputTabs.setFont(UIHelper.FONT_BODY);
        inputTabs.setBackground(UIHelper.BG_PANEL);
        inputTabs.setForeground(UIHelper.TEXT_PRIMARY);

        // Can bo table
        canBoTable = new JTable(new DefaultTableModel(
                new String[]{"ID", "Ma GV", "Ho Ten", "Ngay Sinh", "Don Vi"}, 0));
        UIHelper.styleTable(canBoTable);
        inputTabs.addTab("Danh sach Can bo", UIHelper.createScrollPane(canBoTable));

        // Phong thi table
        phongThiTable = new JTable(new DefaultTableModel(
                new String[]{"STT", "Phong Thi", "Ghi Chu"}, 0));
        UIHelper.styleTable(phongThiTable);
        inputTabs.addTab("Danh sach Phong thi", UIHelper.createScrollPane(phongThiTable));

        panel.add(inputTabs, BorderLayout.CENTER);

        // Send button
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setOpaque(false);

        sendBtn = UIHelper.createButton(">>> GUI PHAN CONG", UIHelper.ACCENT);
        sendBtn.setPreferredSize(new Dimension(220, 42));
        sendBtn.setEnabled(false);
        sendBtn.addActionListener(e -> doSendAssignment());
        btnPanel.add(sendBtn);

        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // === RESULT SECTION ===
    private JPanel createResultSection() {
        JPanel panel = UIHelper.createTitledPanel("KET QUA PHAN CONG");
        panel.setLayout(new BorderLayout(0, 10));

        // Result tabs
        resultTabs = new JTabbedPane();
        resultTabs.setFont(UIHelper.FONT_BODY);
        resultTabs.setBackground(UIHelper.BG_PANEL);
        resultTabs.setForeground(UIHelper.TEXT_PRIMARY);

        // Phan cong table
        phanCongTable = new JTable(new DefaultTableModel(
                new String[]{"STT", "Phong Thi", "Dia diem", "Ma GT1", "Ho ten GT1", "Ma GT2", "Ho ten GT2"}, 0));
        UIHelper.styleTable(phanCongTable);
        resultTabs.addTab("Giam thi", UIHelper.createScrollPane(phanCongTable));

        // Giam sat table
        giamSatTable = new JTable(new DefaultTableModel(
                new String[]{"STT", "Ma GV", "Ho ten", "Don vi", "Tu phong", "Den phong"}, 0));
        UIHelper.styleTable(giamSatTable);
        resultTabs.addTab("Giam sat Hanh lang", UIHelper.createScrollPane(giamSatTable));

        panel.add(resultTabs, BorderLayout.CENTER);

        // Export buttons
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        exportPanel.setOpaque(false);

        exportPhanCongBtn = UIHelper.createButton("[v] Xuat DS Coi thi", UIHelper.PRIMARY);
        exportPhanCongBtn.setEnabled(false);
        exportPhanCongBtn.addActionListener(e -> doExportPhanCong());
        exportPanel.add(exportPhanCongBtn);

        exportGiamSatBtn = UIHelper.createButton("[v] Xuat DS Giam sat", UIHelper.PRIMARY);
        exportGiamSatBtn.setEnabled(false);
        exportGiamSatBtn.addActionListener(e -> doExportGiamSat());
        exportPanel.add(exportGiamSatBtn);

        panel.add(exportPanel, BorderLayout.SOUTH);
        return panel;
    }

    // === STATUS BAR ===
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(25, 28, 35));
        bar.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));

        statusLabel = UIHelper.createLabel("\u26AA Chua ket noi Server", UIHelper.FONT_SMALL, UIHelper.WARNING);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel version = UIHelper.createLabel("v1.0 - He thong Phan cong Can bo Coi thi", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        bar.add(version, BorderLayout.EAST);

        return bar;
    }

    // ======================================================
    // ACTION HANDLERS
    // ======================================================

    private void doConnect() {
        String host = hostField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Port khong hop le!");
            return;
        }

        try {
            connection.connect(host, port);
            statusLabel.setText("\u25CF Da ket noi: " + host + ":" + port);
            statusLabel.setForeground(UIHelper.SUCCESS);
            connectBtn.setEnabled(false);
            disconnectBtn.setEnabled(true);
            updateSendButton();
        } catch (Exception e) {
            showError("Khong the ket noi den Server!\n" + e.getMessage());
        }
    }

    private void doDisconnect() {
        connection.disconnect();
        statusLabel.setText("\u26AA Chua ket noi Server");
        statusLabel.setForeground(UIHelper.WARNING);
        connectBtn.setEnabled(true);
        disconnectBtn.setEnabled(false);
        sendBtn.setEnabled(false);
    }

    private void doChooseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chon file Excel dau vao");
        fc.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            selectedFilePath = file.getAbsolutePath();
            fileLabel.setText(file.getName());
            fileLabel.setForeground(UIHelper.ACCENT);

            // Doc du lieu preview
            try {
                ExcelReader reader = new ExcelReader();
                canBoList = reader.readCanBoList(selectedFilePath);
                phongThiList = reader.readPhongThiList(selectedFilePath);

                // Hien thi can bo
                DefaultTableModel cbModel = (DefaultTableModel) canBoTable.getModel();
                cbModel.setRowCount(0);
                for (CanBo cb : canBoList) {
                    cbModel.addRow(new Object[]{
                            cb.getId(), cb.getMaGV(), cb.getHoTen(),
                            cb.getNgaySinh() != null ? cb.getNgaySinh().toString() : "",
                            cb.getDonVi()
                    });
                }

                // Hien thi phong thi
                DefaultTableModel ptModel = (DefaultTableModel) phongThiTable.getModel();
                ptModel.setRowCount(0);
                int stt = 1;
                for (PhongThi pt : phongThiList) {
                    ptModel.addRow(new Object[]{stt++, pt.getPhongThi(), pt.getGhiChu()});
                }

                // Chi cap nhat label toi da, KHONG ghi de n va m neu nguoi dung da nhap
                nMaxLabel.setText("(file co: " + canBoList.size() + " can bo)");
                mMaxLabel.setText("(file co: " + phongThiList.size() + " phong thi)");

                showInfo("Da doc file thanh cong!\n" +
                         "- File co: " + canBoList.size() + " can bo, " + phongThiList.size() + " phong thi.\n" +
                         "- Hay nhap n (so can bo) va m (so phong thi) truoc khi gui phan cong.");
                updateSendButton();
            } catch (Exception e) {
                showError("Loi doc file Excel!\n" + e.getMessage());
                selectedFilePath = null;
                canBoList = null;
                phongThiList = null;
            }
        }
    }

    private void doSendAssignment() {
        if (!connection.isConnected()) {
            showError("Chua ket noi den Server!");
            return;
        }
        if (canBoList == null || phongThiList == null) {
            showError("Chua chon file du lieu!");
            return;
        }

        // Doc va kiem tra n, m nhap tay
        int n, m;
        try {
            n = Integer.parseInt(nField.getText().trim());
            m = Integer.parseInt(mField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Vui long nhap so nguyen hop le cho n va m!");
            return;
        }

        if (n <= 0 || m <= 0) {
            showError("n va m phai lon hon 0!");
            return;
        }

        if (n > canBoList.size()) {
            showError("n = " + n + " vuot qua so can bo trong file Excel (" + canBoList.size() + ").\nVui long nhap lai!");
            return;
        }

        if (m > phongThiList.size()) {
            showError("m = " + m + " vuot qua so phong thi trong file Excel (" + phongThiList.size() + ").\nVui long nhap lai!");
            return;
        }

        // Kiem tra dieu kien n >= 2*m
        if (n < 2 * m) {
            showError("Khong du can bo coi thi!\n" +
                      "Can it nhat " + (2 * m) + " can bo cho " + m + " phong thi (n >= 2m),\n" +
                      "nhung hien chi co n = " + n + " can bo.\n\n" +
                      "Vui long nhap lai n va m!");
            return;
        }

        // Lay ngau nhien n can bo tu danh sach (cong bang, khong luon lay nhom dau)
        List<CanBo> shuffledCanBo = new java.util.ArrayList<>(canBoList);
        java.util.Collections.shuffle(shuffledCanBo);
        List<CanBo> selectedCanBo = new java.util.ArrayList<>(shuffledCanBo.subList(0, n));

        // Lay m phong thi dau tien (phong thi theo thu tu vi tri)
        List<PhongThi> selectedPhongThi = new java.util.ArrayList<>(phongThiList.subList(0, m));

        sendBtn.setEnabled(false);
        sendBtn.setText("Dang xu ly...");

        // Chay tren background thread
        new Thread(() -> {
            try {
                RequestData data = new RequestData(selectedCanBo, selectedPhongThi);
                Message request = new Message(MessageType.ASSIGNMENT_REQUEST, data);
                Message response = connection.sendRequest(request);

                SwingUtilities.invokeLater(() -> {
                    if (response.getType() == MessageType.ASSIGNMENT_RESULT) {
                        lastResult = (ResultData) response.getData();
                        displayResult(lastResult);
                        showInfo("Phan cong thanh cong! Dot " + lastResult.getDotId() +
                                 "\n- So can bo (n): " + n +
                                 "\n- So phong thi (m): " + m +
                                 "\n- Giam thi: " + (2 * m) + " nguoi" +
                                 "\n- Giam sat hanh lang: " + (n - 2 * m) + " nguoi");
                    } else {
                        showError("Loi tu Server:\n" + response.getData());
                    }
                    sendBtn.setText(">>> GUI PHAN CONG");
                    updateSendButton();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showError("Loi gui du lieu:\n" + e.getMessage());
                    sendBtn.setText(">>> GUI PHAN CONG");
                    updateSendButton();
                });
            }
        }).start();
    }

    private void displayResult(ResultData result) {
        // Hien thi phan cong
        DefaultTableModel pcModel = (DefaultTableModel) phanCongTable.getModel();
        pcModel.setRowCount(0);
        int stt = 1;
        for (PhanCong pc : result.getDanhSachPhanCong()) {
            pcModel.addRow(new Object[]{
                    stt++,
                    pc.getPhongThi().getPhongThi(),
                    pc.getPhongThi().getGhiChu() != null ? pc.getPhongThi().getGhiChu() : "",
                    pc.getGiamThi1().getMaGV(),
                    pc.getGiamThi1().getHoTen(),
                    pc.getGiamThi2().getMaGV(),
                    pc.getGiamThi2().getHoTen()
            });
        }

        // Hien thi giam sat
        DefaultTableModel gsModel = (DefaultTableModel) giamSatTable.getModel();
        gsModel.setRowCount(0);
        stt = 1;
        for (GiamSat gs : result.getDanhSachGiamSat()) {
            gsModel.addRow(new Object[]{
                    stt++,
                    gs.getCanBo().getMaGV(),
                    gs.getCanBo().getHoTen(),
                    gs.getCanBo().getDonVi() != null ? gs.getCanBo().getDonVi() : "",
                    gs.getTuPhong(),
                    gs.getDenPhong()
            });
        }

        exportPhanCongBtn.setEnabled(true);
        exportGiamSatBtn.setEnabled(true);

        // Chuyen sang tab ket qua
        resultTabs.setSelectedIndex(0);
    }

    private void doExportPhanCong() {
        if (lastResult == null) return;
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Luu file Phan cong Coi thi");
        fc.setSelectedFile(new File("PhanCong_Dot" + lastResult.getDotId() + ".xlsx"));
        fc.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".xlsx")) path += ".xlsx";
                new ExcelWriter().exportPhanCong(path, lastResult.getDotId(), lastResult.getDanhSachPhanCong());
                showInfo("Da xuat file: " + path);
            } catch (Exception e) {
                showError("Loi xuat file:\n" + e.getMessage());
            }
        }
    }

    private void doExportGiamSat() {
        if (lastResult == null) return;
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Luu file Giam sat Hanh lang");
        fc.setSelectedFile(new File("GiamSat_Dot" + lastResult.getDotId() + ".xlsx"));
        fc.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String path = fc.getSelectedFile().getAbsolutePath();
                if (!path.endsWith(".xlsx")) path += ".xlsx";
                new ExcelWriter().exportGiamSat(path, lastResult.getDotId(), lastResult.getDanhSachGiamSat());
                showInfo("Da xuat file: " + path);
            } catch (Exception e) {
                showError("Loi xuat file:\n" + e.getMessage());
            }
        }
    }

    private void updateSendButton() {
        sendBtn.setEnabled(connection.isConnected() && canBoList != null && phongThiList != null);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Loi", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thong bao", JOptionPane.INFORMATION_MESSAGE);
    }
}
