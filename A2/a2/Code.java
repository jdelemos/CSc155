package a2;

import java.nio.*;
import java.util.Arrays;

import javax.swing.*;
import java.lang.Math;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_TRIANGLE_FAN;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.common.nio.Buffers;
import org.joml.Matrix4f;

import org.joml.*;

public class Code extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[10];
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private float cubeLocA, cubeLocB, cubeLocC;
	private int pyramidTexture, ellipseTexture, skyTexture, moonTexture;

	private float cx = 0.0f; // Center x
	private float cy = 0.0f; // Center y
	private float cz = -1.0f; // Center z
	private float radius = .7f; // Radius of the circle
	private int segments = 70; // Number of segments (smoothness of the circle)
	private VertexSetup vertexSetup;
	private double circleAngle = 0.0f;
	private float inc = .01f;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16); // buffer for transfering matrix to uniform
	private Matrix4f pMat = new Matrix4f(); // perspective matrix
	private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f mvMat = new Matrix4f(); // model-view matrix
	private int mvLoc, pLoc;
	private float[] ellipsePositions;
	private float[] skyPositions; // Now accessible throughout the class

	private float aspect;

	public Code() {
		setTitle("A2 - 3D Objects - Jonathon Delemos");
		setSize(600, 600);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Ensure both depth and color buffers are cleared

		gl.glUseProgram(renderingProgram);

		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();

		// ----------------------------
		// SET CAMERA POSITION ONCE PER FRAME
		// ----------------------------
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
		vMat.translation(-cameraX, -cameraY, -cameraZ); // Move the world based on the camera

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, pyramidTexture);
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		circleAngle += .005f;
		float x = 5.0f * (float) Math.cos(circleAngle);
		float y = 5.0f * (float) Math.sin(circleAngle);

		drawObject(gl, vbo[0], vbo[2], -2.0f, -1.0f, 0.0f);
		drawObject(gl, vbo[0], vbo[2], 2.7f, -.25f, 0.0f);
		drawObject(gl, vbo[0], vbo[2], .5f, -.50f, 0.0f);
		drawCircle(gl, vbo[6], moonTexture, vbo[7], x, y, -0.10f);
		drawEllipse(gl, vbo[3], ellipseTexture);
		drawEllipse(gl, vbo[5], skyTexture);

	}

	/**
	 * Helper method to draw an object with a given transformation.
	 */
	private void drawObject(GL4 gl, int vboID, int textureCoord, float locX, float locY, float locZ) {
		mMat.identity();
		mMat.translate(locX, locY, locZ); // Set object transformation

		mvMat.identity();
		mvMat.mul(vMat); // Apply camera view
		mvMat.mul(mMat); // Apply object transformation

		// **Bind the texture before drawing**
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, pyramidTexture);

		// Send transformation matrix to the shader
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Bind and draw the object
		gl.glBindBuffer(GL_ARRAY_BUFFER, vboID);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Pyramid texture creation
		gl.glBindBuffer(GL_ARRAY_BUFFER, textureCoord);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		gl.glDrawArrays(GL_TRIANGLES, 0, 18);
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) drawable.getGL();
		renderingProgram = Utils.createShaderProgram("a2/vertShader.glsl", "a2/fragShader.glsl");
		vertexSetup = new VertexSetup(vao, vbo);
		vertexSetup.setupVertices(gl);
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 8.0f;
		pyramidTexture = Utils.loadTexture("pyramid.jpg");
		ellipseTexture = Utils.loadTexture("sand.jpg"); // A new texture for the ellipse
		skyTexture = Utils.loadTexture("sky.jpg");
		moonTexture = Utils.loadTexture("moon.jpg");

	}

	private void drawEllipse(GL4 gl, int vboID, int tex) {

		// Reset the model matrix
		mMat.identity();

		// Translate the ellipse to the bottom of the screen
		mMat.translate(0.0f, 0.0f, 0.0f);

		// Apply the view and model transformations
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		// Bind the ellipse texture
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, tex);
		gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "textureSampler"), 0);

		// Send transformation matrices to the shader
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Bind vertex positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vboID);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Bind texture coordinates
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		// Draw as a triangle fan
		gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 15);
	}

	private void drawCircle(GL4 gl, int vboID, int tex, int textureCoord, float x, float y, float z) {

		// Reset the model matrix
		mMat.identity();

		// Translate the ellipse to the bottom of the screen
		mMat.translate(x, y, z);

		// Apply the view and model transformations
		mvMat.identity();
		mvMat.mul(vMat);
		mvMat.mul(mMat);

		// Bind the ellipse texture
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, tex);
		gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "textureSampler"), 0);

		// Send transformation matrices to the shader
		gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
		gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

		// Bind vertex positions
		gl.glBindBuffer(GL_ARRAY_BUFFER, vboID);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);

		// Bind texture coordinates
		gl.glBindBuffer(GL_ARRAY_BUFFER, textureCoord);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);

		// Draw as a triangle fan
		gl.glDrawArrays(GL_TRIANGLE_FAN, 0, 70);
	}

	public static void main(String[] args) {
		new Code();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}
