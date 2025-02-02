/*
 * ---------------------------------------------------------
 * File: Code.java
 * Author: Jonathon Delemos
 * University: California State University, Sacramento
 * Date: 2/1/2025
 * Assignment: Lab 1 - OpenGL and JOGL
 * ---------------------------------------------------------
 * Description:
 * This is the main entry point for the OpenGL application.
 * It initializes the OpenGL context, handles user input,
 * and manages rendering through the DisplayHandler.
 * ---------------------------------------------------------
 */

package a1;

import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Scanner;
import java.util.Vector;
import com.jogamp.opengl.util.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.lang.Math;

public class Code extends JFrame implements GLEventListener {

    // Variable declarations
    private GLCanvas myCanvas;
    private int renderingProgram;
    private int vao[] = new int[1];
    private long previousTime; // Stores the time of the last frame
    private DisplayHandler displayHandler;
    public KeyInputHandler keyHandler;

    // Default constructor
    public Code() {
        setTitle("A1 - Isosceles Triangle Animations - Jonathon Delemos");
        setSize(600, 400);
        setLocation(200, 200);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        myCanvas.addKeyListener(keyHandler);
        myCanvas.setFocusable(true);
        myCanvas.requestFocus();
        this.add(myCanvas);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                myCanvas.requestFocusInWindow();
            }
        });
        Animator animtr = new Animator(myCanvas);
        animtr.start();
    }

    // Overrides the display
    public void display(GLAutoDrawable drawable) {
        displayHandler.display();
    }

    // initialization function
    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        renderingProgram = ShaderProgram.createShaderProgram();
        gl.glGenVertexArrays(vao.length, vao, 0);
        previousTime = System.nanoTime();
        gl.glBindVertexArray(vao[0]);
        displayHandler = new DisplayHandler(renderingProgram);
        keyHandler = new KeyInputHandler(displayHandler);
        myCanvas.addKeyListener(keyHandler);

    }

    // making the fps readable
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // main driver
    public static void main(String[] args) {
        new Code();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}
