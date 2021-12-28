package com.nyarstot.dinogame.gameserver;

import com.nyarstot.dinogame.engine.math.Vector3f;

public class MessageProtocol {
    // Private

    private String message = "";

    // Public

    public String idPacket(int id) {
        message = "ID: " + id;
        return message;
    }

    public String newClientPacket(Vector3f pos, int id) {
        message = "New Client: " + id + "|" + pos.x + "," + pos.y + "," + pos.z;
        return message;
    }

}
