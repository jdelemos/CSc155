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

package a3;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

public class KeyInputHandler implements KeyListener {

    private DisplayHandler displayHandler;
    private int positionIndex = 0;
    private float rotationSpeed = .01f;
    private float speed = .05f;
    private int rollingNumber = 0;

    // Constructor to initialize DisplayHandler
    public KeyInputHandler(DisplayHandler displayHandler) {
        this.displayHandler = displayHandler;
    }

    // this is pretty messy, but I can't see a way to decrease the lines of code
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            System.out.println("listening.");
            // System.out.println(displayHandler.cameraX);

            displayHandler.moveForward(speed);

        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            displayHandler.moveBackward(speed);
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            displayHandler.pan(rotationSpeed);

        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            displayHandler.pan(-rotationSpeed);

        }
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            displayHandler.pitch(rotationSpeed);

        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            displayHandler.pitch(-rotationSpeed);

        }

        if (e.getKeyCode() == KeyEvent.VK_E) {
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

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            rollingNumber++;
            if (rollingNumber % 2 == 1) {
                displayHandler.setWorldAxes(1);
            } else {
                displayHandler.setWorldAxes(0);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_1) {
            displayHandler.setLightX();
        }
        if (e.getKeyCode() == KeyEvent.VK_2) {
            displayHandler.setLightNegX();
        }
        if (e.getKeyCode() == KeyEvent.VK_3) {
            displayHandler.setLightZ();
        }
        if (e.getKeyCode() == KeyEvent.VK_4) {
            displayHandler.setLightNegZ();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
