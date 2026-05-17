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
    private JTextField nField, mField;
    private JLabel nMaxLabel, mMaxLabel;
    private JButton connectBtn, disconnectBtn;
    private JLabel statusLabel;
    private JLabel fileLabel;
    private JButton chooseFileBtn, sendBtn, exportPhanCongBtn, exportGiamSatBtn;
    private JTable canBoTable, phongThiTable, phanCongTable, giamSatTable;
    private JTabbedPane inputTabs, resultTabs;

    // Stat card value labels
    private JLabel statNValue, statMValue, statCoiThiValue, statGiamSatValue;
    private int lastN = 0, lastM = 0;

    private String selectedFilePath = null;
    private List<CanBo> canBoList = null;
    private List<PhongThi> phongThiList = null;
    private ResultData lastResult = null;

    public MainFrame() {
        connection = new ClientConnection();
        initUI();
    }

    private void initUI() {
        setTitle("H\u1ec6 TH\u1ed0NG PH\u00c2N C\u00d4NG C\u00c1N B\u1ed8 COI THI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG_APP);
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
        header.setBackground(UIHelper.HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIHelper.BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 25, 14, 25)
        ));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setOpaque(false);
        JLabel title = UIHelper.createLabel(
                "H\u1ec6 TH\u1ed0NG PH\u00c2N C\u00d4NG C\u00c1N B\u1ed8 COI THI",
                UIHelper.FONT_TITLE, UIHelper.PRIMARY_DARK);
        JLabel subtitle = UIHelper.createLabel(
                "T\u1ef1 \u0111\u1ed9ng ph\u00e2n c\u00f4ng gi\u00e1m th\u1ecb, gi\u00e1m s\u00e1t v\u00e0 xu\u1ea5t danh s\u00e1ch Excel",
                UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.WEST);

        JPanel connPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        connPanel.setOpaque(false);
        connPanel.add(UIHelper.createLabel("Host:", UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
        hostField = UIHelper.createTextField(10);
        hostField.setText("localhost");
        connPanel.add(hostField);
        connPanel.add(UIHelper.createLabel("Port:", UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
        portField = UIHelper.createTextField(5);
        portField.setText("9999");
        connPanel.add(portField);

        connectBtn = UIHelper.createButton("K\u1ebft n\u1ed1i", UIHelper.PRIMARY);
        connectBtn.setPreferredSize(new Dimension(110, 34));
        connectBtn.addActionListener(e -> doConnect());
        connPanel.add(connectBtn);

        disconnectBtn = UIHelper.createButton("Ng\u1eaft", UIHelper.ERROR);
        disconnectBtn.setPreferredSize(new Dimension(85, 34));
        disconnectBtn.setEnabled(false);
        disconnectBtn.addActionListener(e -> doDisconnect());
        connPanel.add(disconnectBtn);

        header.add(connPanel, BorderLayout.EAST);
        return header;
    }

    // === MAIN PANEL ===
    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBackground(UIHelper.BG_APP);
        main.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        // Left: Input section
        JPanel leftPanel = createInputSection();
        leftPanel.setPreferredSize(new Dimension(500, 0));

        // Right: Result section
        JPanel rightPanel = createResultSection();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(500);
        splitPane.setDividerSize(6);
        splitPane.setBorder(null);
        splitPane.setBackground(UIHelper.BG_APP);

        main.add(splitPane, BorderLayout.CENTER);
        return main;
    }

    // === INPUT SECTION ===
    private JPanel createInputSection() {
        JPanel panel = UIHelper.createSectionPanel(null, "D\u1eee LI\u1ec6U \u0110\u1ea6U V\u00c0O");
        // Override layout since createSectionPanel uses BorderLayout with header at NORTH
        // We need to add our content to CENTER and SOUTH
        JPanel content = new JPanel(new BorderLayout(0, 10));
        content.setOpaque(false);

        // Top area: file chooser + m/n input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        // File chooser area
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        filePanel.setOpaque(false);
        chooseFileBtn = UIHelper.createButton("Ch\u1ecdn file Excel", UIHelper.PRIMARY);
        chooseFileBtn.setPreferredSize(new Dimension(160, 34));
        chooseFileBtn.addActionListener(e -> doChooseFile());
        filePanel.add(chooseFileBtn);
        fileLabel = UIHelper.createLabel("Ch\u01b0a ch\u1ecdn file", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        filePanel.add(fileLabel);
        topPanel.add(filePanel);

        // n, m input area
        JPanel mnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        mnPanel.setOpaque(false);
        mnPanel.add(UIHelper.createLabel("n (S\u1ed1 c\u00e1n b\u1ed9):", UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
        nField = UIHelper.createTextField(5);
        mnPanel.add(nField);
        nMaxLabel = UIHelper.createLabel("", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        mnPanel.add(nMaxLabel);
        mnPanel.add(Box.createHorizontalStrut(10));
        mnPanel.add(UIHelper.createLabel("m (S\u1ed1 ph\u00f2ng thi):", UIHelper.FONT_BODY, UIHelper.TEXT_PRIMARY));
        mField = UIHelper.createTextField(5);
        mnPanel.add(mField);
        mMaxLabel = UIHelper.createLabel("", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        mnPanel.add(mMaxLabel);
        topPanel.add(mnPanel);

        // Condition label with background
        JPanel condPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        condPanel.setBackground(new Color(255, 251, 235));
        condPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 230, 138), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        JLabel condLabel = UIHelper.createLabel(
                "\u0110i\u1ec1u ki\u1ec7n: 2m < n <= 3m. Gi\u00e1m s\u00e1t = n - 2m.",
                UIHelper.FONT_SMALL, new Color(146, 64, 14));
        condPanel.add(condLabel);
        topPanel.add(condPanel);

        content.add(topPanel, BorderLayout.NORTH);

        // Input data preview tabs
        inputTabs = new JTabbedPane();
        inputTabs.setFont(UIHelper.FONT_BODY);
        inputTabs.setBackground(UIHelper.BG_PANEL);
        inputTabs.setForeground(UIHelper.TEXT_PRIMARY);

        canBoTable = new JTable(new DefaultTableModel(
                new String[]{"ID", "M\u00e3 GV", "H\u1ecd t\u00ean", "Ng\u00e0y sinh", "\u0110\u01a1n v\u1ecb"}, 0));
        UIHelper.styleTable(canBoTable);
        inputTabs.addTab("Danh s\u00e1ch c\u00e1n b\u1ed9", UIHelper.createScrollPane(canBoTable));

        phongThiTable = new JTable(new DefaultTableModel(
                new String[]{"STT", "Ph\u00f2ng thi", "Ghi ch\u00fa"}, 0));
        UIHelper.styleTable(phongThiTable);
        inputTabs.addTab("Danh s\u00e1ch ph\u00f2ng thi", UIHelper.createScrollPane(phongThiTable));

        content.add(inputTabs, BorderLayout.CENTER);

        // Send button - full width
        JPanel btnPanel = new JPanel(new BorderLayout(0, 0));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        sendBtn = UIHelper.createButton("G\u1eecI PH\u00c2N C\u00d4NG", UIHelper.SUCCESS);
        sendBtn.setPreferredSize(new Dimension(0, 44));
        sendBtn.setEnabled(false);
        sendBtn.addActionListener(e -> doSendAssignment());
        btnPanel.add(sendBtn, BorderLayout.CENTER);
        content.add(btnPanel, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // === RESULT SECTION ===
    private JPanel createResultSection() {
        JPanel panel = UIHelper.createSectionPanel(null, "K\u1ebeT QU\u1ea2 PH\u00c2N C\u00d4NG");
        JPanel content = new JPanel(new BorderLayout(0, 8));
        content.setOpaque(false);

        resultTabs = new JTabbedPane();
        resultTabs.setFont(UIHelper.FONT_BODY);
        resultTabs.setBackground(UIHelper.BG_PANEL);
        resultTabs.setForeground(UIHelper.TEXT_PRIMARY);

        phanCongTable = new JTable(new DefaultTableModel(
                new String[]{"STT", "Ph\u00f2ng thi", "\u0110\u1ecba \u0111i\u1ec3m", "M\u00e3 GT1", "H\u1ecd t\u00ean GT1", "M\u00e3 GT2", "H\u1ecd t\u00ean GT2"}, 0));
        UIHelper.styleTable(phanCongTable);
        resultTabs.addTab("Gi\u00e1m th\u1ecb (Coi thi)", UIHelper.createScrollPane(phanCongTable));

        giamSatTable = new JTable(new DefaultTableModel(
                new String[]{"STT", "M\u00e3 GV", "H\u1ecd t\u00ean", "\u0110\u01a1n v\u1ecb", "T\u1eeb ph\u00f2ng", "\u0110\u1ebfn ph\u00f2ng"}, 0));
        UIHelper.styleTable(giamSatTable);
        resultTabs.addTab("Gi\u00e1m s\u00e1t h\u00e0nh lang", UIHelper.createScrollPane(giamSatTable));

        content.add(resultTabs, BorderLayout.CENTER);

        // Bottom: stats + export
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        // Stat cards row
        JPanel statPanel = new JPanel(new GridLayout(1, 4, 8, 0));
        statPanel.setOpaque(false);
        statPanel.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));

        JPanel card1 = UIHelper.createStatCard("user.png", "T\u1ed5ng c\u00e1n b\u1ed9 ch\u1ecdn (n)", "--", "", UIHelper.PRIMARY);
        JPanel card2 = UIHelper.createStatCard("building.jpg", "S\u1ed1 ph\u00f2ng thi (m)", "--", "", UIHelper.ACCENT);
        JPanel card3 = UIHelper.createStatCard("teacher.png", "C\u00e1n b\u1ed9 coi thi", "--", "", UIHelper.PURPLE);
        JPanel card4 = UIHelper.createStatCard("security.jpg", "Gi\u00e1m s\u00e1t h\u00e0nh lang", "--", "", UIHelper.WARNING);

        // Get value labels (3rd component in each card's BoxLayout)
        statNValue = (JLabel) card1.getComponent(5);
        statMValue = (JLabel) card2.getComponent(5);
        statCoiThiValue = (JLabel) card3.getComponent(5);
        statGiamSatValue = (JLabel) card4.getComponent(5);

        statPanel.add(card1);
        statPanel.add(card2);
        statPanel.add(card3);
        statPanel.add(card4);
        bottomPanel.add(statPanel);

        // Export buttons
        JPanel exportPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        exportPanel.setOpaque(false);
        exportPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        exportPhanCongBtn = UIHelper.createOutlinedButton("Xu\u1ea5t DS Gi\u00e1m th\u1ecb (Coi thi)", UIHelper.PRIMARY);
        exportPhanCongBtn.setPreferredSize(new Dimension(0, 38));
        exportPhanCongBtn.setEnabled(false);
        exportPhanCongBtn.addActionListener(e -> doExportPhanCong());
        exportPanel.add(exportPhanCongBtn);

        exportGiamSatBtn = UIHelper.createOutlinedButton("Xu\u1ea5t DS Gi\u00e1m s\u00e1t h\u00e0nh lang", UIHelper.PRIMARY);
        exportGiamSatBtn.setPreferredSize(new Dimension(0, 38));
        exportGiamSatBtn.setEnabled(false);
        exportGiamSatBtn.addActionListener(e -> doExportGiamSat());
        exportPanel.add(exportGiamSatBtn);

        bottomPanel.add(exportPanel);
        content.add(bottomPanel, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    // === STATUS BAR ===
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIHelper.STATUSBAR_BG);
        bar.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));

        statusLabel = UIHelper.createLabel("Ch\u01b0a k\u1ebft n\u1ed1i Server", UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel version = UIHelper.createLabel("v1.0 \u2014 H\u1ec7 th\u1ed1ng Ph\u00e2n c\u00f4ng C\u00e1n b\u1ed9 Coi thi",
                UIHelper.FONT_SMALL, UIHelper.TEXT_SECONDARY);
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
                nMaxLabel.setText("(" + canBoList.size() + " can bo)");
                mMaxLabel.setText("(" + phongThiList.size() + " phong thi)");

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

        // Kiem tra dieu kien 2*m < n <= 3*m
        if (n <= 2 * m || n > 3 * m) {
            showError("\u0110i\u1ec1u ki\u1ec7n kh\u00f4ng h\u1ee3p l\u1ec7!\n" +
                      "Y\u00eau c\u1ea7u: 2m < n <= 3m\n" +
                      "V\u1edbi m = " + m + ": c\u1ea7n " + (2*m) + " < n <= " + (3*m) + "\n" +
                      "Hi\u1ec7n t\u1ea1i n = " + n);
            return;
        }

        int soCoiThi = 2 * m;
        int soGiamSat = n - soCoiThi;

        // Lay ngau nhien n can bo tu danh sach (cong bang, khong luon lay nhom dau)
        List<CanBo> shuffledCanBo = new java.util.ArrayList<>(canBoList);
        java.util.Collections.shuffle(shuffledCanBo);
        List<CanBo> selectedCanBo = new java.util.ArrayList<>(shuffledCanBo.subList(0, n));

        // Lay m phong thi dau tien (phong thi theo thu tu vi tri)
        List<PhongThi> selectedPhongThi = new java.util.ArrayList<>(phongThiList.subList(0, m));

        lastN = n;
        lastM = m;

        sendBtn.setEnabled(false);
        sendBtn.setText("\u0110ang x\u1eed l\u00fd...");

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
                        showInfo("Ph\u00e2n c\u00f4ng th\u00e0nh c\u00f4ng! \u0110\u1ee3t " + lastResult.getDotId() +
                                 "\n- S\u1ed1 c\u00e1n b\u1ed9 ch\u1ecdn (n): " + n +
                                 "\n- S\u1ed1 ph\u00f2ng thi (m): " + m +
                                 "\n- C\u00e1n b\u1ed9 coi thi: " + soCoiThi + " ng\u01b0\u1eddi" +
                                 "\n- Gi\u00e1m s\u00e1t h\u00e0nh lang: " + soGiamSat + " ng\u01b0\u1eddi");
                    } else {
                        showError("Loi tu Server:\n" + response.getData());
                    }
                    sendBtn.setText("G\u1eecI PH\u00c2N C\u00d4NG");
                    updateSendButton();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    showError("Loi gui du lieu:\n" + e.getMessage());
                    sendBtn.setText("G\u1eecI PH\u00c2N C\u00d4NG");
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

        // Update stat cards
        int soCoiThi = 2 * lastM;
        int soGiamSat = lastN - soCoiThi;
        statNValue.setText(String.valueOf(lastN));
        statMValue.setText(String.valueOf(lastM));
        statCoiThiValue.setText(String.valueOf(soCoiThi));
        statGiamSatValue.setText(String.valueOf(soGiamSat));

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
