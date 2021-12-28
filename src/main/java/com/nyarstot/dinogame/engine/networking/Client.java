package com.nyarstot.dinogame.engine.networking;

import com.nyarstot.dinogame.engine.math.Vector3f;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    // Private

    private Socket clientSocket;
    private String hostName;

    private int port;
    private DataOutputStream writer;
    private MessageProtocol packetManager;

    private static Client client;

    private Client() throws IOException {
        packetManager = new MessageProtocol();
    }

    // Public

    public void register(String ip, int port, Vector3f position) throws IOException {
        this.port = port;
        this.hostName = ip;
        clientSocket = new Socket(ip, port);

        writer = new DataOutputStream(clientSocket.getOutputStream());
        writer.writeUTF(packetManager.packetRegister(position));
    }

    public void sendToServer(String message) {
        if (message.equals("\\exit")) {
            System.exit(0);
        } else {
            try {
                Socket socket = new Socket(hostName, port);
                System.out.println("Server: " + message);
                writer = new DataOutputStream(socket.getOutputStream());
                writer.writeUTF(message);
            } catch (IOException e) {
                System.err.println("Data can't be send to server");
                e.printStackTrace();
            }
        }
    }

    public static Client getClientInstance() {
        if (client == null) {
            try {
                client = new Client();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return client;
    }

    public Socket getClientSocket() { return clientSocket; }
}
