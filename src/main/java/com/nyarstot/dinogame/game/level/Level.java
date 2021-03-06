package com.nyarstot.dinogame.game.level;

import com.nyarstot.dinogame.engine.graphics.Shader;
import com.nyarstot.dinogame.engine.graphics.Texture;
import com.nyarstot.dinogame.engine.graphics.VertexArray;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.engine.math.Vector3f;
import com.nyarstot.dinogame.engine.networking.Client;
import com.nyarstot.dinogame.engine.networking.MessageProtocol;
import com.nyarstot.dinogame.game.player.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Level {
    // Private

    private int xScroll = 0;
    private int map = 0;

    private boolean control = true;

    private VertexArray background;
    private Texture bgTexture;

    private Obstacle[] obstacles = new Obstacle[10];
    private int index = 0;
    private final float OFFSET = 10.0f;

    private Client client;
    private boolean gameStatus = true;

    private Ground ground;
    private ArrayList<Player> players;
    private Player player = null;


    private boolean collision() {
        for (int i = 0; i < 10; i++) {
            float px = -xScroll * 0.1f;
            float py = player.getY();
            float ox = obstacles[i].getX();
            float oy = obstacles[i].getY();

            float px0 = px - player.getSize() / 2.0f;
            float px1 = px + player.getSize() / 2.0f;
            float py0 = py - player.getSize() / 2.0f;
            float py1 = py + player.getSize() / 2.0f;

            float ox0 = ox;
            float ox1 = ox + Obstacle.getWidth();
            float oy0 = oy;
            float oy1 = oy + Obstacle.getHeight();

            if (px1 > ox0 && px0 < ox1) {
                if (py1 > oy0 && py0 < oy1) {
                    return true;
                }
            }
        }
        return false;
    }

    private Random random = new Random();
    private void createObstacles() {
        Obstacle.create();
        for (int i = 0; i < 10; i++) {
            obstacles[i] = new Obstacle(OFFSET + index * 10.f, -3f);
            index++;
        }
    }

    private void updateObstacles() {
        obstacles[index % 10] = new Obstacle(OFFSET + index * 10.f, -3f);
        index++;
    }

    private void renderObstacles() {
        Shader.FIR.enable();
        Shader.FIR.setUniformMat4f("vw_matrix", Matrix4f.translate(new Vector3f(xScroll * 0.15f, 0.f, 0.f)));
        Obstacle.getTexture().bind();
        Obstacle.getMesh().bind();
        for (int i = 0; i < 10; i++) { // Model matrix
            Shader.FIR.setUniformMat4f("ml_matrix", obstacles[i].getModelMatrix());
            Obstacle.getMesh().draw();
        }
        Obstacle.getTexture().unbind();
        Obstacle.getMesh().unbind();
    }

    // Public

    public Level() {
        players = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            players.add(null);
        }

        client = Client.getClientInstance();

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
//        player = new Player();

        createObstacles();
    }

    public void update() {
        if (gameStatus == true) {
            xScroll--;
            if (-xScroll % 600 == 0) {
                map++;
            }
            if (-xScroll > 50 && -xScroll % 100 == 0) {
                updateObstacles();
            }
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i) != null) {
                    players.get(i).update();
                    client.sendToServer(new MessageProtocol().packetUpdate(players.get(i).getPosition(), players.get(i).getId()));
                }
            }
//            player.update();
        }
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
        renderObstacles();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i) != null) {
                players.get(i).render();
            }
        }
//        player.render();
    }

    public void registerNewPlayer(Player newPlayer) {
        if (newPlayer.getId() == 2) {
            newPlayer.setPosition(new Vector3f(-8.5f, -2.2f, 0f));
        }
        players.set(newPlayer.getId(), newPlayer);
    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public void removePlayer(int id) {
        players.set(id, null);
    }
}
