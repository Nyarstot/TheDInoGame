package com.nyarstot.dinogame.engine.engine;

import com.nyarstot.dinogame.engine.graphics.Shader;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.engine.math.Vector3f;
import com.nyarstot.dinogame.engine.networking.Client;
import com.nyarstot.dinogame.game.level.Level;
import com.nyarstot.dinogame.game.player.Player;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.nyarstot.dinogame.engine.IO.Input;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Engine implements Runnable{
    // Private

    private long window;
    private final int width = 800;
    private final int height = 600;

    private Thread thread;
    private boolean running = false;

    private Level level;

    // CLIENT STUFF
    private Player player;
    private Client client;

    private void init() {
        System.out.println("Initializing...");

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(width, height, "DinoGame", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert vidMode != null;
        glfwSetWindowPos(
                window,
                (vidMode.width() - width)/ 2,
                (vidMode.height() - height) / 2
        );

        glfwSetKeyCallback(window, new Input());

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GL.createCapabilities();
        glClearColor(255.0f, 255.0f, 255.0f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        Shader.loadAll();

        Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
        Shader.BG.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.BG.setUniform1i("bg_texture", 1);

        Shader.PLAYER.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.PLAYER.setUniform1i("player_texture", 1);

        Shader.GROUND.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.GROUND.setUniform1i("ground_texture", 1);

        Shader.FIR.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.FIR.setUniform1i("fir_texture", 1);

        player = new Player();
        level = new Level();

        client = Client.getClientInstance();
        tryRegisterClient();
    }

    private void update() {
        glfwPollEvents();
        level.update();
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        level.render();
        glfwSwapBuffers(window);
    }

    // Public

    public void runEngine() {
        System.out.println("Starting engine...");

        running = true;
        thread = new Thread(this, "game");
        thread.start();
    }

    // Runs game loop
    public void run() {
        init();

        long  lastTime = System.nanoTime();
        double delta = 0.0;
        double ns = 1000000000.0/60.0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        GL.createCapabilities();

        System.out.println("Engine started successfully");
        System.out.println("\nOpenGL: " + glGetString(GL_VERSION));
        System.out.println("GLFW: " + glfwGetVersionString());

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1.0) {
                update();
                updates++;
                delta--;
            }
            render();
            frames++;
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                //System.out.println(updates + " updates | " + frames + "  fps");
                frames = 0;
                updates = 0;
            }

            if (glfwWindowShouldClose(window)) {
                running = false;
            }
        }
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public void tryRegisterClient() {
        try {
            client.register("localhost", 6066, new Vector3f());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new ClientReceivingThread(client.getClientSocket()).start();
        } catch (IOException e) {
            System.err.println("Server error: The server is not running");
            e.printStackTrace();
        }
    }

    public class ClientReceivingThread extends Thread {
        // Private

        private Socket clientSocket;
        private DataInputStream reader;

        // Public

        public ClientReceivingThread(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                reader = new DataInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (running) {
                String message = "";
                System.out.println("Engine socket message stack: " + message);
                try {
                    message = reader.readUTF();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }

                if (message.startsWith("ID: ")) {
                    int id = Integer.parseInt(message.substring(4));
                    player.setId(id);
                    System.out.println("My id is " + id);
                } else if (message.startsWith("New Client: ")) {
                    // New Client: 1|5,2,3
                    int charPos1 = message.indexOf('|');
                    int charPos2 = message.indexOf(',');
                    int charPos3 = message.indexOf(',', charPos2 + 1);

                    int id = Integer.parseInt(message.substring(12, charPos1));
                    Vector3f position = new Vector3f(
                            Float.parseFloat(message.substring(charPos1 + 1, charPos2)),
                            Float.parseFloat(message.substring(charPos2 + 1, charPos3)),
                            Float.parseFloat(message.substring(charPos3 + 1, message.length()))
                    );
                    if (id != player.getId()) {
                        level.registerNewPlayer(player);
                    }
                } else if (message.startsWith("\\upd: ")) {
                    int charPos1 = message.indexOf('|');
                    int charPos2 = message.indexOf(',');
                    int charPos3 = message.indexOf(',', charPos2 + 1);

                    int id = Integer.parseInt(message.substring(6, charPos1));
                    Vector3f position = new Vector3f(
                            Float.parseFloat(message.substring(charPos1 + 1, charPos2)),
                            Float.parseFloat(message.substring(charPos2 + 1, charPos3)),
                            Float.parseFloat(message.substring(charPos3 + 1, message.length()))
                    );

                    if (id != player.getId()) {
                        level.getPlayer(id).setPosition(position);
                        level.update();
                    }
                } else if (message.startsWith("\\rmv ")) {
                    int id = Integer.parseInt(message.substring(5));

                    if (id == player.getId()) {
                        System.out.println("REMOVE");
                    } else {
                        level.removePlayer(id);
                    }

                } else if (message.startsWith("\\exit ")) {
                    int id = Integer.parseInt(message.substring(6));

                    if (id != player.getId()) {
                        level.removePlayer(id);
                    }
                }
            }

            try {
                reader.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
