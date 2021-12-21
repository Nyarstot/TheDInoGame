package com.nyarstot.dinogame.engine.engine;

import com.nyarstot.dinogame.engine.graphics.Shader;
import com.nyarstot.dinogame.engine.math.Matrix4f;
import com.nyarstot.dinogame.game.level.Level;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.nyarstot.dinogame.engine.IO.Input;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
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
        Shader.loadAll();

        Shader.BG.enable();
        Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
        Shader.BG.setUniformMat4f("pr_matrix", pr_matrix);
        Shader.BG.disable();

        level = new Level();
    }

    private void update() {
        glfwPollEvents();
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
        GL.createCapabilities();

        System.out.println("Engine started successfully");
        System.out.println("\nOpenGL: " + glGetString(GL_VERSION));
        System.out.println("GLFW: " + glfwGetVersionString());

        while (running) {
            update();
            render();

            if (glfwWindowShouldClose(window)) {
                running = false;
            }
        }
    }
}
