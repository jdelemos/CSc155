
/*
 * ---------------------------------------------------------
 * File: DisplayHandler.java
 * Author: Jonathon Delemos
 * University: California State University, Sacramento
 * Date: 2/21/2025
 * Assignment: Lab 2 - OpenGL and JOGL
 * ---------------------------------------------------------
 * Description:
 * This class handles the rendering process in OpenGL.
 * It updates transformations, manages color switching,
 * and sends uniform data to the shaders.
 * ---------------------------------------------------------
 */

package a2;

import java.nio.*;
import java.util.Arrays;
import javax.swing.*;
import java.lang.Math;
import static com.jogamp.opengl.GL.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import java.awt.event.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Code extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int renderingProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[15];
	private float cameraX, cameraY, cameraZ;
	private int pyramidTexture, ellipseTexture, skyTexture, moonTexture,
			cloudTexture, redTextureColor, greenTextureColor, blueTextureColor;
	private double circleAngle = 0.0f;
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4f pMat = new Matrix4f();
	private Matrix4f vMat = new Matrix4f();
	private Matrix4f mMat = new Matrix4f();
	private Matrix4f mvMat = new Matrix4f();
	private Vector3f cameraU = new Vector3f();
	private Vector3f cameraV = new Vector3f();
	private Vector3f cameraN = new Vector3f();
	private int mvLoc, pLoc;
	private float aspect;
	private DisplayHandler displayHandler;
	public KeyInputHandler keyHandler;

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
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		circleAngle += 0.001f;
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		displayHandler.display(gl, displayHandler.cameraX, displayHandler.cameraY, displayHandler.cameraZ, aspect,
				circleAngle);

	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) drawable.getGL();
		renderingProgram = Utils.createShaderProgram("a2/vertShader.glsl", "a2/fragShader.glsl");
		mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix");
		pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");

		VertexSetup vertexSetup = new VertexSetup(vao, vbo);
		vertexSetup.setupVertices(gl);
		cameraX = 0.0f;
		cameraY = 0.0f;
		cameraZ = 8.0f;
		cameraU.set(1.0f, 0.0f, 0.0f); // Right direction
		cameraV.set(0.0f, 1.0f, 0.0f); // Up direction
		cameraN.set(0.0f, 0.0f, -1.0f); // Forward direction
		pyramidTexture = Utils.loadTexture("pyramid.jpg");
		ellipseTexture = Utils.loadTexture("sand.jpg");
		skyTexture = Utils.loadTexture("sky.jpg");
		moonTexture = Utils.loadTexture("moon.jpg");
		cloudTexture = Utils.loadTexture("cloud.jpeg");
		redTextureColor = Utils.loadTexture("RedX.jpg");
		greenTextureColor = Utils.loadTexture("green.jpg");
		blueTextureColor = Utils.loadTexture("blue.JPG");

		displayHandler = new DisplayHandler(renderingProgram, vbo, mvLoc, pLoc, pMat, vMat, mMat, mvMat, vals,
				pyramidTexture, ellipseTexture, skyTexture, moonTexture, cloudTexture, cameraX, cameraY, cameraZ,
				cameraU, cameraV, cameraN, redTextureColor, greenTextureColor, blueTextureColor);
		keyHandler = new KeyInputHandler(displayHandler);
		myCanvas.addKeyListener(keyHandler);
		myCanvas.requestFocusInWindow();
	}

	public static void main(String[] args) {
		new Code();
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
	}

	public void dispose(GLAutoDrawable drawable) {
	}
}