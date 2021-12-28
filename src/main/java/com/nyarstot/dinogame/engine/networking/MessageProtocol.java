package com.nyarstot.dinogame.engine.networking;

import com.nyarstot.dinogame.engine.math.Vector3f;

public class MessageProtocol {
    // Private

    private String message = "";

    // Public

    public String packetRegister(Vector3f position) {
        message = "\\jn: " + position.x + "," + position.y + "," + position.z;
        return message;
    }

    public String packetUpdate(Vector3f position, int id) {
        message = "\\upd: " + id + "|" + position.x + "," + position.y + "," + position.z;
        return message;
    }

    public String packetRemoveClient(int id) {
        message = "\\rmv " + id;
        return message;
    }

    public String packetExit(int id) {
        message = "\\exit " + id;
        return message;
    }

}
