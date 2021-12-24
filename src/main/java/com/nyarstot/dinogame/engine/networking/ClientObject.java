package com.nyarstot.dinogame.engine.networking;

public class ClientObject {
    // Private

    private String address;
    private int port;
    private int id;

    // Public

    public ClientObject(String address, int port, int id) {
        this.address = address;
        this.port = port;
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getId() {
        return id;
    }

}
