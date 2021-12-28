package com.nyarstot.dinogame.gameserver;

import java.io.IOException;
import java.net.SocketException;

public class ServerMain {
    public static void main(String[] strings) {
        try {
            Server server = new Server();
            server.start();
        } catch (SocketException e) {
            System.err.println("Couldn't start server");
        }
    }
}
