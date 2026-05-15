package client;

import common.protocol.Message;

import java.io.*;
import java.net.Socket;

/**
 * Quan ly ket noi TCP tu Client den Server.
 */
public class ClientConnection {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean connected = false;

    /**
     * Ket noi den Server
     */
    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(socket.getInputStream());
        connected = true;
        System.out.println("[Client] Ket noi thanh cong den " + host + ":" + port);
    }

    /**
     * Gui request va nhan response tu Server
     */
    public Message sendRequest(Message request) throws IOException, ClassNotFoundException {
        if (!connected) {
            throw new IOException("Chua ket noi den Server!");
        }
        oos.writeObject(request);
        oos.flush();
        return (Message) ois.readObject();
    }

    /**
     * Dong ket noi
     */
    public void disconnect() {
        try {
            connected = false;
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("[Client] Da ngat ket noi.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
}
