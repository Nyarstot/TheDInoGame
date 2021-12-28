package com.nyarstot.dinogame.gameserver;

import com.nyarstot.dinogame.engine.math.Vector3f;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread{
    // Private

    private ArrayList<ClientObject> clients;
    private ServerSocket serverSocket;
    private int port = 6066;

    private DataOutputStream writer;
    private DataInputStream reader;

    MessageProtocol protocol;
    private boolean running = true;

    // Public

    public Server() throws SocketException {
        clients = new ArrayList<ClientObject>();
        protocol = new MessageProtocol();
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Socket clientSocket = null;
        while (running) {
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String message = "";
            try {
                reader = new DataInputStream(clientSocket.getInputStream());
                message = reader.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Server message: " + message);
            if (message.startsWith("\\jn: ")) {
                int charPos1 = message.indexOf(',');
                int charPos2 = message.indexOf(',', charPos1 + 1);

                Vector3f position = new Vector3f(
                        Float.parseFloat(message.substring(5, charPos1)),
                        Float.parseFloat(message.substring(charPos1 + 1, charPos2)),
                        Float.parseFloat(message.substring(charPos2 + 1))
                );

                try {
                    writer = new DataOutputStream(clientSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendToClient(protocol.idPacket(clients.size() + 1));
                clients.add(new ClientObject(writer, position));
                try {
                    broadcastMessage(protocol.newClientPacket(position, clients.size() + 1));
                    sendAllClients(writer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (message.startsWith("\\upd: ")) {
                int charPos1 = message.indexOf('|');
                int charPos2 = message.indexOf(',');
                int charPos3 = message.indexOf(',', charPos2 + 1);

                int id = Integer.parseInt(message.substring(6, charPos1));
                Vector3f position = new Vector3f(
                        Float.parseFloat(message.substring(charPos1 + 1, charPos2)),
                        Float.parseFloat(message.substring(charPos2 + 1, charPos3)),
                        Float.parseFloat(message.substring(charPos3 + 1))
                );

                if (clients.get(id) != null) {
                    clients.get(id - 1).setClientPosition(position);
                    try {
                        broadcastMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (message.startsWith("\\rmv: ")) {
                int id = Integer.parseInt(message.substring(6));

                try {
                    broadcastMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (message.startsWith("\\exit: ")) {
                int id = Integer.parseInt(message.substring(7));

                try {
                    broadcastMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (clients.get(id - 1) != null) {
                    clients.set(id - 1, null);
                }
            }
        }

        try {
            reader.close();
            writer.close();
            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void shutdown() {
        running = false;
    }

    public void broadcastMessage(String message) throws IOException {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i) != null) {
                clients.get(i).getWriter().writeUTF(message);
            }
        }
    }

    public void sendToClient(String message) {
        if (message.equals("exit")) {
            System.exit(0);
        }
        else {
            try {
                writer.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendAllClients(DataOutputStream writer) {
        Vector3f position = new Vector3f();
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i) != null) {
                position = clients.get(i).getClientPosition();
                try {
                    writer.writeUTF(protocol.newClientPacket(position, i + 1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
