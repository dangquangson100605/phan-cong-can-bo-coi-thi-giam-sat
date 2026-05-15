package server;

import server.db.DatabaseManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Entry point cho Server.
 * Lang nghe ket noi tu Client tren port 9999.
 */
public class ServerMain {
    private static final int PORT = 9999;
    private static boolean running = true;

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  HE THONG PHAN CONG CAN BO COI THI");
        System.out.println("  SERVER - Port " + PORT);
        System.out.println("===========================================");

        // Khoi tao database
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.initializeDatabase();
            System.out.println("[Server] Database da san sang.");
        } catch (Exception e) {
            System.err.println("[Server] LOI: Khong the ket noi database!");
            System.err.println("[Server] Chi tiet: " + e.getMessage());
            System.err.println("[Server] Hay kiem tra MySQL da chay chua.");
            return;
        }

        // Khoi tao ServerSocket
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] Dang lang nghe tren port " + PORT + "...");
            System.out.println("[Server] Cho ket noi tu Client...");
            System.out.println("-------------------------------------------");

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String clientAddr = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("[Server] Client ket noi: " + clientAddr + ":" + clientSocket.getPort());

                    // Tao thread xu ly client
                    ClientHandler handler = new ClientHandler(clientSocket);
                    handler.start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("[Server] Loi khi chap nhan ket noi: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[Server] Khong the khoi tao ServerSocket: " + e.getMessage());
        } finally {
            DatabaseManager.getInstance().disconnect();
        }
    }

    public static void shutdown() {
        running = false;
    }
}
