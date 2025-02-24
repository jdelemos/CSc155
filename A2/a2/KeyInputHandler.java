/*
 * ---------------------------------------------------------
 * File: KeyInputHandler.java
 * Author: Jonathon Delemos
 * University: California State University, Sacramento
 * Date: 2/23/2025
 * Assignment: Lab 2 - OpenGL and JOGL
 * ---------------------------------------------------------
 * Description:
 * This class Key Input Listener process in OpenGL.
 * It updates camera position, rotates point of view, and passes
 * variables to the Display Handler class. 
 * ---------------------------------------------------------
 */

package a2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputHandler implements KeyListener {

    private DisplayHandler displayHandler;
    private int positionIndex = 0;
    private float rotationSpeed = .05f;
    private float speed = .05f;

    // Constructor to initialize DisplayHandler
    public KeyInputHandler(DisplayHandler displayHandler) {
        this.displayHandler = displayHandler;
    }

    // this is pretty messy, but I can't see a way to decrease the lines of code
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            System.out.println("listening.");
            System.out.println(displayHandler.cameraX);
            displayHandler.moveBackward(speed);

        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            displayHandler.moveForward(speed);
        }

        if (e.getKeyCode() == KeyEvent.VK_3) {
            // displayHandler.rotateCamera(rotationSpeed);

        }

        if (e.getKeyCode() == KeyEvent.VK_R) {
            displayHandler.moveDown(speed);
        }

        if (e.getKeyCode() == KeyEvent.VK_Q) {
            displayHandler.moveUp(speed);
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            displayHandler.moveLeft(speed);
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            displayHandler.moveRight(speed);
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
