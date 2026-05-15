package server;

import common.model.*;
import common.protocol.*;
import server.service.AssignmentService;
import server.db.DatabaseManager;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Xu ly moi ket noi client trong thread rieng.
 */
public class ClientHandler extends Thread {
    private final Socket clientSocket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(clientSocket.getInputStream());

            // Doc message tu client
            while (!clientSocket.isClosed()) {
                try {
                    Message request = (Message) ois.readObject();
                    System.out.println("[Handler] Nhan request: " + request.getType());

                    Message response = processRequest(request);
                    oos.writeObject(response);
                    oos.flush();
                    System.out.println("[Handler] Da gui response: " + response.getType());
                } catch (EOFException e) {
                    System.out.println("[Handler] Client ngat ket noi.");
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("[Handler] Loi: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    /**
     * Xu ly request tu client va tra ve response
     */
    private Message processRequest(Message request) {
        try {
            switch (request.getType()) {
                case ASSIGNMENT_REQUEST:
                    return handleAssignmentRequest(request);
                case HISTORY_REQUEST:
                    return handleHistoryRequest(request);
                default:
                    return new Message(MessageType.ERROR, "Loai request khong hop le.");
            }
        } catch (Exception e) {
            System.err.println("[Handler] Loi xu ly request: " + e.getMessage());
            return new Message(MessageType.ERROR, e.getMessage());
        }
    }

    /**
     * Xu ly yeu cau phan cong
     */
    private Message handleAssignmentRequest(Message request) {
        try {
            RequestData data = (RequestData) request.getData();
            List<CanBo> canBoList = data.getDanhSachCanBo();
            List<PhongThi> phongThiList = data.getDanhSachPhongThi();

            System.out.println("[Handler] Du lieu: " + canBoList.size() + " can bo, " + phongThiList.size() + " phong thi.");

            AssignmentService service = new AssignmentService();
            ResultData result = service.phanCong(canBoList, phongThiList);

            return new Message(MessageType.ASSIGNMENT_RESULT, result);
        } catch (Exception e) {
            return new Message(MessageType.ERROR, e.getMessage());
        }
    }

    /**
     * Xu ly yeu cau xem lich su
     */
    private Message handleHistoryRequest(Message request) {
        try {
            DatabaseManager db = DatabaseManager.getInstance();
            List<Map<String, Object>> dots = db.getAllDotPhanCong();

            // Neu co dot_id cu the, tra chi tiet
            if (request.getData() != null && request.getData() instanceof Integer) {
                int dotId = (Integer) request.getData();
                List<PhanCong> pcList = db.getPhanCongByDot(dotId);
                List<GiamSat> gsList = db.getGiamSatByDot(dotId);
                ResultData result = new ResultData(dotId, pcList, gsList);
                return new Message(MessageType.HISTORY_RESULT, result);
            }

            // Tra danh sach tat ca cac dot
            return new Message(MessageType.HISTORY_RESULT, (Serializable) dots);
        } catch (Exception e) {
            return new Message(MessageType.ERROR, e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            System.out.println("[Handler] Da dong ket noi client.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
