package com.nyarstot.dinogame.game.level;

import com.nyarstot.dinogame.engine.graphics.Texture;
import com.nyarstot.dinogame.engine.graphics.VertexArray;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.engine.math.Vector3f;

public class Obstacle {

    // Private

    private Vector3f position = new Vector3f();
    private Matrix4f ml_matrix;

    private static float width = 1.5f, height = 1.5f;
    private static VertexArray mesh;

    private static Texture default_texture;

    // Public

    public static void create() {
        float[] vertices = new float[] {
                0.0f,  0.0f,  0.2f,
                0.0f,  height, 0.2f,
                width, height, 0.2f,
                width, 0.0f,  0.2f
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
        default_texture = new Texture("res/obstacles/fir.png");

    }

    public Obstacle(float x, float y) {
        position.x = x;
        position.y = y;

        ml_matrix = Matrix4f.translate(position);
    }

    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public static VertexArray getMesh() { return mesh; }
    public static Texture getTexture()  { return default_texture; }
    public static float getWidth()  { return width; }
    public static float getHeight() { return height; }
    public Matrix4f getModelMatrix() { return ml_matrix; }

}
