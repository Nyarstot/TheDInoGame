package com.nyarstot.dinogame.engine.networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Server {
    // Private

    private int port;
    private boolean online;
    private DatagramSocket socket;

    private ArrayList<ClientObject> clients = new ArrayList<ClientObject>();
    private int clientID = 0;

    private String lastMessage;

    // Public

    public Server(int port) {
        try {
            this.port = port;
            socket = new DatagramSocket(port);
            online = true;

            receive();      // Wait for client connection message
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receive() {
        Thread serverThread = new Thread() {
            public void run() {
                try {
                    byte[] rawData = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(rawData, rawData.length);
                    socket.receive(packet);

                    String message = new String(rawData);
                    message = message.substring(0, message.indexOf("\\e"));

                    if (!commandCorrect(message, packet)) {
                        lastMessage = message;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        serverThread.run();
    }

    // Basically sanding message to all clients
    public void send(String message, String ip, int port) {
        try {
            message = message + "\\e";
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(ip), port);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateClients(String message) {
        for (int i = 0; i < clients.size(); i++) {
            ClientObject client = clients.get(i);
            send(message, client.getAddress(), client.getPort());
        }
    }

    public boolean commandCorrect(String message, DatagramPacket packet) {
        if (message.startsWith("\\c")) {
            // Connect client to the server
            ClientObject client = new ClientObject(
                    packet.getAddress().toString(),
                    packet.getPort(),
                    clientID+1
            );
            clients.add(client);
            send("\\cid: " + client.getId(), client.getAddress(), client.getPort());

            clientID++;
            return true;
        } else if (message.startsWith("\\d:")) {
            // Disconnect client
            int id = Integer.parseInt(message.substring(3));
            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).getId() == id) {
                    clients.remove(i);
                    return true;
                }
            }
            System.err.println("Server error: failed to remove client with unknown id: " + id);
            // TODO: Make error logger to pass this kind of message
            return true;
        }
        return false;
    }

    public int getPort() {
        return port;
    }

    public boolean isOnline() {
        return online;
    }

    public ArrayList<ClientObject> getClients() {
        return clients;
    }

    public String getMessage() {
        String message = lastMessage;
        lastMessage = null;
        return message;
    }
}
