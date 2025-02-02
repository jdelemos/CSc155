/*
 * ---------------------------------------------------------
 * File: DisplayHandler.java
 * Author: Jonathon Delemos
 * University: California State University, Sacramento
 * Date: 2/1/2025
 * Assignment: Lab 1 - OpenGL and JOGL
 * ---------------------------------------------------------
 * Description:
 * This class handles the rendering process in OpenGL.
 * It updates transformations, manages color switching,
 * and sends uniform data to the shaders.
 * ---------------------------------------------------------
 */

package a1;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

public class DisplayHandler {

    // variables that are being used by the shader files
    private int renderingProgram;
    private int colorIndex = 0;
    private int flipkey = 1; // Default value (same as in `Code.java`)
    private float t = 0.0f;
    private float x = 0.0f, tanValue = 0.0f, inc = 0.01f, circlenagle = 0.0f;
    private float vertex0width = 0.5f;
    private float vertex0height = -0.25f;
    private float vertex1width = -0.5f;
    private float vertex1height = -0.25f;
    private float vertex2width = 0;
    private float vertex2height = 0.25f;
    private int flipbit = 0;
    private long previousTime;

    // no parameter function that will create the display called in main Code
    public DisplayHandler(int renderingProgram) {
        this.renderingProgram = renderingProgram;
        this.previousTime = System.nanoTime();
    }

    public void display() {
        float[][] colors = {
                { 1.0f, 1.0f, 0.0f, 1.0f }, // Yellow
                { 1.0f, 0.0f, 1.0f, 1.0f }, // Purple
                { 0.0f, 0.0f, 1.0f, 1.0f } // Blue
        };

        float[][] gradientColors = {
                { 1.0f, 0.0f, 0.0f, 1.0f }, // Red
                { 0.0f, 0.0f, 1.0f, 1.0f }, // Blue
                { 0.0f, 1.0f, 0.0f, 1.0f } // Green
        };

        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);

        long currentTime = System.nanoTime();
        previousTime = currentTime;

        // using a flip variable to change the movement of the Isosceles triangle
        if (flipkey % 2 == 0) {
            circlenagle += inc;
            x = (float) Math.cos(circlenagle);
            tanValue = (float) Math.sin(circlenagle);
            if (circlenagle >= 360) {
                circlenagle = 0;
            }

        } else {
            circlenagle += inc;
            x = (float) Math.cos(circlenagle);
            tanValue = 0;
            if (circlenagle >= 360) {
                circlenagle = 0;
            }
        }

        int colorsLoc = gl.glGetUniformLocation(renderingProgram, "uColors");
        // using a loop to read in all the colors
        if (colorIndex < 2) {
            for (int i = 0; i < colors.length; i++) {
                gl.glProgramUniform4fv(renderingProgram, colorsLoc + i, 1, colors[i], 0);
            }
        }
        // if colorIndex is on two, we read in an array of gradient colors and send that
        // to the fragment shader
        else {
            for (int i = 0; i < gradientColors.length; i++) {
                gl.glProgramUniform4fv(renderingProgram, colorsLoc + i, 1, gradientColors[i], 0);
            }
        }
        // sending the position to the vertex shader
        int offsetLoc = gl.glGetUniformLocation(renderingProgram, "offset");
        int width1Loc = gl.glGetUniformLocation(renderingProgram, "vertex0width");
        int width2Loc = gl.glGetUniformLocation(renderingProgram, "vertex1width");
        int width3Loc = gl.glGetUniformLocation(renderingProgram, "vertex2width");
        int height0Loc = gl.glGetUniformLocation(renderingProgram, "vertex0height");
        int height1Loc = gl.glGetUniformLocation(renderingProgram, "vertex1height");
        int height2Loc = gl.glGetUniformLocation(renderingProgram, "vertex2height");
        gl.glProgramUniform1f(renderingProgram, height0Loc, vertex0height);
        gl.glProgramUniform1f(renderingProgram, height1Loc, vertex1height);
        gl.glProgramUniform1f(renderingProgram, height2Loc, vertex2height);
        gl.glProgramUniform1f(renderingProgram, offsetLoc, x);
        gl.glProgramUniform1f(renderingProgram, width1Loc, vertex0width);
        gl.glProgramUniform1f(renderingProgram, width2Loc, vertex1width);
        gl.glProgramUniform1f(renderingProgram, width3Loc, vertex2width);
        // sending gradient values to the fragment shader
        int tLoc = gl.glGetUniformLocation(renderingProgram, "t");
        if (colorIndex == 2) {
            t += 0.01f;
            if (t > 1.0f) {
                t = 0.0f;
            }
        }
        gl.glProgramUniform1f(renderingProgram, tLoc, t);

        int offsetLoc2 = gl.glGetUniformLocation(renderingProgram, "tanValue");
        int colorIndexLoc = gl.glGetUniformLocation(renderingProgram, "colorIndex");
        gl.glProgramUniform1i(renderingProgram, colorIndexLoc, colorIndex);
        gl.glProgramUniform1f(renderingProgram, offsetLoc2, tanValue);

        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    // Getter and Setter for colorIndex
    public void setColorIndex(int colorIndex) {
        this.colorIndex = colorIndex;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    // Getter and Setter for flipkey
    public void setFlipKey(int flipkey) {
        this.flipkey = flipkey;
    }

    public int getFlipKey() {
        return flipkey;
    }

    // Getter and Setter for vertex0width
    public void setVertex0Width(float vertex0width) {
        this.vertex0width = vertex0width;
    }

    public float getVertex0Width() {
        return vertex0width;
    }

    // Getter and Setter for vertex1width
    public void setVertex1Width(float vertex1width) {
        this.vertex1width = vertex1width;
    }

    public float getVertex1Width() {
        return vertex1width;
    }

    // Getter and Setter for vertex2width
    public void setVertex2Width(float vertex2width) {
        this.vertex2width = vertex2width;
    }

    public float getVertex2Width() {
        return vertex2width;
    }

    // Getter and Setter for vertex0height (formerly height2)
    public void setVertex0Height(float vertex0height) {
        this.vertex0height = vertex0height;
    }

    public float getVertex0Height() {
        return vertex0height;
    }

    // Getter and Setter for vertex1height (formerly height3)
    public void setVertex1Height(float vertex1height) {
        this.vertex1height = vertex1height;
    }

    public float getVertex1Height() {
        return vertex1height;
    }

    // Getter and Setter for vertex2height (formerly height1)
    public void setVertex2Height(float vertex2height) {
        this.vertex2height = vertex2height;
    }

    public float getVertex2Height() {
        return vertex2height;
    }

}
