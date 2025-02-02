/*
 * ---------------------------------------------------------
 * File: KeyInputHandler.java
 * Author: Jonathon Delemos
 * University: California State University, Sacramento
 * Date: 2/1/2025
 * Assignment: Lab 1 - OpenGL and JOGL
 * ---------------------------------------------------------
 * Description:
 * This class Key Input Listener process in OpenGL.
 * It updates buttons presseed, updates vertex variables,
 * and sends uniform data to the shaders.
 * ---------------------------------------------------------
 */

package a1;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputHandler implements KeyListener {

    private DisplayHandler displayHandler;
    private int positionIndex = 0;

    // Constructor to initialize DisplayHandler
    public KeyInputHandler(DisplayHandler displayHandler) {
        this.displayHandler = displayHandler;
    }

    // this is pretty messy, but I can't see a way to decrease the lines of code
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1) {
            int newFlipKey = displayHandler.getFlipKey() + 1;
            displayHandler.setFlipKey(newFlipKey);
            System.out.println("FlipKey: " + newFlipKey);
        }

        if (e.getKeyCode() == KeyEvent.VK_2) {
            int newColorIndex = (displayHandler.getColorIndex() + 1) % 3;
            displayHandler.setColorIndex(newColorIndex);
        }

        if (e.getKeyCode() == KeyEvent.VK_3) {
            float newVertex0Width = displayHandler.getVertex0Width() + 0.01f;
            float newVertex1Width = displayHandler.getVertex1Width() - 0.01f;
            displayHandler.setVertex0Width(newVertex0Width);
            displayHandler.setVertex1Width(newVertex1Width);
        }

        if (e.getKeyCode() == KeyEvent.VK_4) {
            float newVertex0Width = displayHandler.getVertex0Width() - 0.01f;
            float newVertex1Width = displayHandler.getVertex1Width() + 0.01f;
            displayHandler.setVertex0Width(newVertex0Width);
            displayHandler.setVertex1Width(newVertex1Width);
        }

        if (e.getKeyCode() == KeyEvent.VK_5) {
            positionIndex++;
            if (positionIndex == 1) {
                displayHandler.setVertex2Height(-.75f);
            } else if (positionIndex == 2) {

                // vertex 0
                displayHandler.setVertex0Width(0.25f);
                displayHandler.setVertex0Height(.75f);

                // vertex 1
                displayHandler.setVertex1Width(0f);
                displayHandler.setVertex1Height(0);

                // vertex 2
                displayHandler.setVertex2Width(.25f);
                displayHandler.setVertex2Height(-.75f);

            } else if (positionIndex == 3) {

                // vertex 1
                displayHandler.setVertex1Width(.5f);

            } else {
                positionIndex = 0;
                // Set initial vertex positions using displayHandler
                displayHandler.setVertex0Width(0.5f);
                displayHandler.setVertex0Height(-0.25f);
                displayHandler.setVertex1Width(-0.5f);
                displayHandler.setVertex1Height(-0.25f);
                displayHandler.setVertex2Width(0.0f);
                displayHandler.setVertex2Height(0.25f);
            }
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}
