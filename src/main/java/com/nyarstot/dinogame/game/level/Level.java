package com.nyarstot.dinogame.game.level;

import com.nyarstot.dinogame.engine.graphics.Shader;
import com.nyarstot.dinogame.engine.graphics.Texture;
import com.nyarstot.dinogame.engine.graphics.VertexArray;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.engine.math.Vector3f;
import com.nyarstot.dinogame.game.player.Player;
import org.w3c.dom.Text;

public class Level {
    // Private

    private VertexArray background;
    private Texture bgTexture;
    private Texture groundTexture;

    private int xScroll = 0;
    private int map = 0;

    private float difficulty = 0.03f;

    private Ground ground;
    private Player player;

    // Public

    public Level() {
        float[] vertices = new float[] {
                -10.0f, -10.0f * 9.0f / 16.0f, 0.0f,
                -10.0f,  10.0f * 9.0f / 16.0f, 0.0f,
                  0.0f,  10.0f * 9.0f / 16.0f, 0.0f,
                  0.0f, -10.0f * 9.0f / 16.0f, 0.0f
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

        background = new VertexArray(vertices, indices, tcs);
        bgTexture = new Texture("res/backgrounds/bg_mountains.png");

        ground = new Ground();
        player = new Player();
    }

    public void update() {
        xScroll--;
        if (-xScroll % 600 == 0) {
            map++;
        }

        player.update();
    }

    public void render() {

        bgTexture.bind();
        Shader.BG.enable();
        background.bind();
        for (int i = map; i < map + 15; i++) {
            Shader.BG.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(i * 10 + xScroll * 0.04f, 0.0f, 0.0f)));
            background.draw();
        }
        background.render();
        Shader.BG.disable();
        bgTexture.unbind();

        ground.render();
        player.render();
    }
}
