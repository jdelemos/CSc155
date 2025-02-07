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

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_VERSION;
import static com.jogamp.opengl.GL4.*;
import java.nio.FloatBuffer;

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
    private int printVersion = 0;

    Package JOGLPack = Package.getPackage("com.jogamp.opengl");

    // no parameter function that will create the display called in main Code
    public DisplayHandler(int renderingProgram) {
        this.renderingProgram = renderingProgram;
        this.previousTime = System.nanoTime();
    }

    public void display() {

        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);

        long currentTime = System.nanoTime();
        long deltaTime = currentTime - previousTime; // Time difference in nanoseconds
        double ELAPSEDTime = deltaTime / 1_000_000_000.0; // Convert to seconds
        previousTime = currentTime; // Update previous time for next frame
        double fps = 1_000_000_000.0 / deltaTime; // Convert to FPS

        // meets requirement one : Get and display the current JOGL, OpenGL, Java
        // versions
        // on the console.
        if (printVersion == 0) {
            Code.clearConsole();
            System.out.println("JOGL VERSION: " + gl.glGetString(GL_VERSION));
            System.out.println("JOML VERSION: " + JOGLPack.getImplementationVersion());
            System.out.println("JAVA VERSION: " + System.getProperty("java.version"));
            System.out.println("ELAPSED TIME: " + ELAPSEDTime);
            System.out.println("FPS: " + (int) fps); //

        }

        // Update rotation and translation values, meeting
        if (flipkey % 2 == 0) {
            circlenagle += ELAPSEDTime;
            x = (float) Math.cos(circlenagle);
            tanValue = (float) Math.sin(circlenagle);
            if (circlenagle >= 360) {
                circlenagle = 0;
            }
        } else {
            circlenagle += ELAPSEDTime;
            x = (float) Math.cos(circlenagle);
            tanValue = 0;
            if (circlenagle >= 360) {
                circlenagle = 0;
            }
        }

        // Define solid colors
        float[][] colors = {
                { 1.0f, 1.0f, 0.0f, 1.0f }, // Yellow (colorIndex = 0)
                { 1.0f, 0.0f, 1.0f, 1.0f } // Purple (colorIndex = 1)
        };

        // Pass `colorIndex` to the shader
        int colorIndexLoc = gl.glGetUniformLocation(renderingProgram, "colorIndex");
        gl.glProgramUniform1i(renderingProgram, colorIndexLoc, colorIndex);

        // Send solid colors to the shader if needed
        if (colorIndex < 2) {
            int colorsLoc = gl.glGetUniformLocation(renderingProgram, "uColors");
            for (int i = 0; i < colors.length; i++) {
                gl.glProgramUniform4fv(renderingProgram, colorsLoc + i, 1, colors[i], 0);

            }
        }

        // Define vertex data based on `colorIndex`
        float[] vertices;

        if (colorIndex == 2) {
            // Gradient mode (per-vertex colors)
            vertices = new float[] {
                    // Vertex 0: Top (Red)
                    0.0f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f,
                    // Vertex 1: Bottom-left (Blue)
                    -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 1.0f,
                    // Vertex 2: Bottom-right (Green)
                    0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f
            };
        } else {
            // Solid color mode (all vertices have the same color)
            float[] solidColor = colors[colorIndex];
            vertices = new float[] {
                    0.0f, 0.5f, solidColor[0], solidColor[1], solidColor[2], solidColor[3],
                    -0.5f, -0.5f, solidColor[0], solidColor[1], solidColor[2], solidColor[3],
                    0.5f, -0.5f, solidColor[0], solidColor[1], solidColor[2], solidColor[3]
            };
        }

        // Create and bind VBO
        int[] vbo = new int[1];
        gl.glGenBuffers(1, vbo, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL_ARRAY_BUFFER, vertices.length * 4, FloatBuffer.wrap(vertices), GL_STATIC_DRAW);

        // Position Attribute
        gl.glVertexAttribPointer(0, 2, GL_FLOAT, false, 6 * 4, 0);
        gl.glEnableVertexAttribArray(0);

        // Color Attribute
        gl.glVertexAttribPointer(1, 4, GL_FLOAT, false, 6 * 4, 2 * 4);
        gl.glEnableVertexAttribArray(1);
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

        int offsetLoc2 = gl.glGetUniformLocation(renderingProgram, "tanValue");
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
