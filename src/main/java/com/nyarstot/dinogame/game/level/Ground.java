package com.nyarstot.dinogame.game.level;

import com.nyarstot.dinogame.engine.graphics.Shader;
import com.nyarstot.dinogame.engine.graphics.Texture;
import com.nyarstot.dinogame.engine.graphics.VertexArray;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.engine.math.Vector3f;

public class Ground {
    // Private

    private  float size = 10.0f;
    private VertexArray mesh;

    private Texture texture;

    private final Vector3f position = new Vector3f(0.f, -2.7f, 0.0f);

    // Public

    public Ground() {
        float[] vertices = new float[] {
                - size, -0.1f, 0.1f,
                - size,  0.1f, 0.1f,
                  size,  0.1f, 0.1f,
                  size, -0.1f,  0.1f
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
        texture = new Texture("res/generic/ground.png");
    }

    public void render() {
        Shader.GROUND.enable();
        Shader.GROUND.setUniformMat4f("ml_matrix", Matrix4f.translate(position));
        texture.bind();
        mesh.render();
        Shader.GROUND.disable();
    }

}
