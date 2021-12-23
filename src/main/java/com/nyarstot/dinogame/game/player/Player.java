package com.nyarstot.dinogame.game.player;

import com.nyarstot.dinogame.engine.IO.Input;
import com.nyarstot.dinogame.engine.graphics.Shader;
import com.nyarstot.dinogame.engine.graphics.Texture;
import com.nyarstot.dinogame.engine.graphics.VertexArray;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.engine.math.Vector3f;

import java.util.Timer;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    // Private

    private final Vector3f PLAYER_NULL_VECTOR = new Vector3f(-5.2f, -2.2f, 0.0f);
    private static final float PLAYER_TOP_Y = -0.5f, PLAYER_BOTTOM_Y = -2.2f;

    private boolean controlEnabled = true;

    private float size = 1.0f;
    private VertexArray mesh;

    private Texture texture;

    private Vector3f position = PLAYER_NULL_VECTOR;
    private float rot;
    private float delta = 0;

    // Public

    public int score;

    public Player() {
        float[] vertices = new float[] {
                - size / 1.5f, -size / 1.5f, 0.1f,
                - size / 1.5f,  size / 1.5f, 0.1f,
                  size / 1.5f,  size / 1.5f, 0.1f,
                  size / 1.5f, -size / 1.5f, 0.1f
        };

        byte[] indices = new byte[] {
                0, 1, 2,
                2, 3, 0
        };

        float[] tcs = new float[] {
                0, 1,
                0, 0,
                1, 0,
                1, 1
        };

        mesh = new VertexArray(vertices, indices, tcs);
        texture = new Texture("res/sprites/dino-stand.png");
    }

    public void update() {
        calculateGravity();
        if (Input.isKeyDown(GLFW_KEY_SPACE)) {
            controlEnabled = false;
            position.y += 0.25f;
        }
    }

    public void calculateGravity() {
        if (position.y <= -2.2f) {
            delta = 0;
        } else if (position.y > -2.2f) {
            delta += 0.009;
        }
        position.y -= delta;
    }

    public void render() {
        Shader.PLAYER.enable();
        Shader.PLAYER.setUniformMat4f("ml_matrix", Matrix4f.translate(position));
        texture.bind();
        mesh.render();
        Shader.PLAYER.disable();
    }
}
