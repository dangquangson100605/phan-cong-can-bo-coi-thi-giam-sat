package client;

import client.ui.MainFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

/**
 * Entry point cho Client.
 * Khoi tao FlatLaf Look & Feel va hien thi giao dien chinh.
 */
public class ClientMain {
    public static void main(String[] args) {
        // Thiet lap Look and Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("TabbedPane.showTabSeparators", true);
        } catch (Exception e) {
            System.err.println("[Client] Khong the thiet lap FlatLaf: " + e.getMessage());
        }

        // Khoi tao giao dien tren EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
