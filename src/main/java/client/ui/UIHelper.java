package client.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Utilities cho giao dien Swing - colors, fonts, borders.
 */
public class UIHelper {

    // === COLOR PALETTE ===
    public static final Color PRIMARY = new Color(41, 98, 255);
    public static final Color PRIMARY_DARK = new Color(24, 71, 199);
    public static final Color PRIMARY_LIGHT = new Color(100, 149, 255);
    public static final Color ACCENT = new Color(0, 200, 150);
    public static final Color BG_DARK = new Color(30, 33, 40);
    public static final Color BG_PANEL = new Color(40, 44, 55);
    public static final Color BG_CARD = new Color(50, 55, 68);
    public static final Color BG_INPUT = new Color(60, 65, 80);
    public static final Color TEXT_PRIMARY = new Color(240, 240, 245);
    public static final Color TEXT_SECONDARY = new Color(160, 165, 180);
    public static final Color SUCCESS = new Color(76, 175, 80);
    public static final Color WARNING = new Color(255, 167, 38);
    public static final Color ERROR = new Color(244, 67, 54);
    public static final Color BORDER_COLOR = new Color(70, 75, 90);

    // === FONTS ===
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Tao JButton dep
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 38));

        // Hover effect
        Color hoverColor = bgColor.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
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
     * Tao JTextField dep
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setBackground(BG_INPUT);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(TEXT_PRIMARY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    /**
     * Tao panel voi border va tieu de
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BG_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(
                                BorderFactory.createEmptyBorder(),
                                title,
                                javax.swing.border.TitledBorder.LEFT,
                                javax.swing.border.TitledBorder.TOP,
                                FONT_SUBTITLE,
                                PRIMARY_LIGHT
                        ),
                        BorderFactory.createEmptyBorder(5, 10, 10, 10)
                )
        ));
        return panel;
    }

    /**
     * Style cho JTable
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Header style
        table.getTableHeader().setFont(FONT_SUBTITLE);
        table.getTableHeader().setBackground(BG_DARK);
        table.getTableHeader().setForeground(PRIMARY_LIGHT);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
    }

    /**
     * Style cho JScrollPane
     */
    public static JScrollPane createScrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBackground(BG_PANEL);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        return sp;
    }
}
