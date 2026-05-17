package client.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URL;

/**
 * Utilities cho giao dien Swing - Light theme, modern desktop style.
 */
public class UIHelper {

    // === COLOR PALETTE ===
    public static final Color PRIMARY = new Color(37, 99, 235);        // #2563EB
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);
    public static final Color ACCENT = new Color(16, 185, 129);        // #10B981
    public static final Color PURPLE = new Color(139, 92, 246);        // #8B5CF6

    public static final Color BG_APP = new Color(245, 247, 251);       // #F5F7FB
    public static final Color BG_PANEL = new Color(255, 255, 255);
    public static final Color BG_CARD = new Color(255, 255, 255);
    public static final Color BG_INPUT = new Color(255, 255, 255);

    public static final Color HEADER_BG = new Color(255, 255, 255);
    public static final Color STATUSBAR_BG = new Color(255, 255, 255);

    public static final Color TEXT_PRIMARY = new Color(31, 41, 55);    // #1F2937
    public static final Color TEXT_SECONDARY = new Color(107, 114, 128);// #6B7280

    public static final Color SUCCESS = new Color(16, 185, 129);       // #10B981
    public static final Color WARNING = new Color(245, 158, 11);       // #F59E0B
    public static final Color ERROR = new Color(239, 68, 68);          // #EF4444
    public static final Color BORDER_COLOR = new Color(229, 231, 235); // #E5E7EB

    // Backward compat aliases
    public static final Color BG_DARK = BG_APP;

    // === FONTS ===
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_STAT_VALUE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_STAT_LABEL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Tao JButton hien dai voi disabled state
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            public void setEnabled(boolean b) {
                super.setEnabled(b);
                setForeground(b ? Color.WHITE : new Color(156, 163, 175));
                setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isEnabled()) {
                    g2.setColor(getBackground());
                } else {
                    g2.setColor(Color.WHITE); // White background when disabled
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 36));

        Color hoverColor = bgColor.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    /**
     * Tao outlined button
     */
    public static JButton createOutlinedButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            public void setEnabled(boolean b) {
                super.setEnabled(b);
                setForeground(b ? color : new Color(156, 163, 175));
                if (b) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(color, 1),
                            BorderFactory.createEmptyBorder(6, 16, 6, 16)
                    ));
                } else {
                    setBorder(BorderFactory.createEmptyBorder(7, 17, 7, 17));
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isEnabled()) {
                    g2.setColor(getBackground());
                } else {
                    g2.setColor(Color.WHITE); // White background when disabled
                }
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setContentAreaFilled(false);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        btn.setPreferredSize(new Dimension(180, 36));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(new Color(245, 247, 251));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled()) btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    /**
     * Tao JLabel
     */
    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    /**
     * Tao JTextField hien dai
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return tf;
    }

    /**
     * Tao section panel voi icon va tieu de
     */
    public static JPanel createSectionPanel(String icon, String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 18, 15, 18)
        ));
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        headerRow.setOpaque(false);
        if (icon != null && !icon.isEmpty()) {
            headerRow.add(createLabel(icon, FONT_SECTION, PRIMARY));
        }
        headerRow.add(createLabel(title, FONT_SECTION, PRIMARY));
        panel.add(headerRow, BorderLayout.NORTH);
        return panel;
    }

    /**
     * Backward compat
     */
    public static JPanel createTitledPanel(String title) {
        return createSectionPanel(null, title);
    }

    /**
     * Style cho JTable - light theme
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.getTableHeader().setFont(FONT_SUBTITLE);
        table.getTableHeader().setBackground(new Color(249, 250, 251));
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
    }

    /**
     * Tao JScrollPane cho table
     */
    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_PANEL);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return sp;
    }

    /**
     * Tao stat card cho panel ket qua
     */
    public static JPanel createStatCard(String iconStr, String label,
                                    String value, String sub,
                                    Color iconColor) {

    JPanel card = new JPanel();
    card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
    card.setBackground(Color.WHITE);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(12, 10, 12, 10)
    ));

    // ===== Load icon =====
    ImageIcon icon = null;

    if (iconStr != null && !iconStr.isEmpty()) {
        URL url = UIHelper.class.getResource("/icons/" + iconStr);

        if (url != null) {
            ImageIcon rawIcon = new ImageIcon(url);

            Image scaledImage = rawIcon.getImage().getScaledInstance(
                    24, 24, Image.SCALE_SMOOTH
            );

            icon = new ImageIcon(scaledImage);
        } else {
            System.out.println("Khong tim thay icon: " + iconStr);
        }
    }

    // ===== Icon label =====
    JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
    iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== Panel nền tròn cho icon =====
    JPanel iconPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(new Color(245, 245, 245)); // xám nhạt
            g2.fillOval(0, 0, getWidth(), getHeight());

            g2.dispose();
        }
    };

    iconPanel.setOpaque(false);
    iconPanel.setLayout(new GridBagLayout());
    iconPanel.setPreferredSize(new Dimension(48, 48));
    iconPanel.setMaximumSize(new Dimension(48, 48));
    iconPanel.setMinimumSize(new Dimension(48, 48));
    iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    iconPanel.add(iconLbl);

    // ===== Label =====
    JLabel labelLbl = new JLabel(label, SwingConstants.CENTER);
    labelLbl.setFont(FONT_STAT_LABEL);
    labelLbl.setForeground(TEXT_SECONDARY);
    labelLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== Value =====
    JLabel valueLbl = new JLabel(value, SwingConstants.CENTER);
    valueLbl.setFont(FONT_STAT_VALUE);
    valueLbl.setForeground(iconColor);
    valueLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== Sub text =====
    JLabel subLbl = new JLabel(sub, SwingConstants.CENTER);
    subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    subLbl.setForeground(TEXT_SECONDARY);
    subLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

    // ===== Add components =====
    card.add(Box.createVerticalGlue());
    card.add(iconPanel);
    card.add(Box.createVerticalStrut(8));
    card.add(labelLbl);
    card.add(Box.createVerticalStrut(4));
    card.add(valueLbl);
    card.add(Box.createVerticalStrut(2));
    card.add(subLbl);
    card.add(Box.createVerticalGlue());

    return card;
}
}
