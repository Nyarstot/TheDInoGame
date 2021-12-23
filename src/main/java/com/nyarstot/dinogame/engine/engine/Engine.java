package com.nyarstot.dinogame.engine.engine;

import com.nyarstot.dinogame.engine.graphics.Shader;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.game.level.Level;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.nyarstot.dinogame.engine.IO.Input;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Engine implements Runnable{
    // Private

    private int width = 800;
    private int height = 600;

    private Thread thread;
    private boolean running = false;

    private long window;

    private Level level;

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

        level = new Level();
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

    // Run game loop
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
                System.out.println(updates + " updates | " + frames + "  fps");
                frames = 0;
                updates = 0;
            }

            if (glfwWindowShouldClose(window)) {
                running = false;
            }
        }
    }
}
