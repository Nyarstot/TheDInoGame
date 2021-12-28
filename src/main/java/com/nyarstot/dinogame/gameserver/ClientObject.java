package com.nyarstot.dinogame.gameserver;

import com.nyarstot.dinogame.engine.math.Vector3f;

import java.io.DataOutputStream;

public class ClientObject {
    // Private

    private DataOutputStream writer;
    Vector3f clientPosition;

    // Public

    public  ClientObject(DataOutputStream writer, Vector3f position) {
        this.writer = writer;
        this.clientPosition = position;
    }

    public void setClientPosition(Vector3f position) { clientPosition = position; }
    public DataOutputStream getWriter() { return writer; }
    public Vector3f getClientPosition() { return clientPosition; }

}
