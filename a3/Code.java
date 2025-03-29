package a3;

import java.nio.*;
import javax.swing.*;
import java.lang.Math;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.awt.GLCanvas;

import com.jogamp.common.nio.Buffers;
import org.joml.*;

public class Code extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private int renderingProgram, normalMapProgram;
	private int skyboxProgram;
	private int vao[] = new int[1];
	private int vbo[] = new int[15];
	private Vector3f torusLoc = new Vector3f(0, 0, -1);
	private DisplayHandler displayHandler;
	private KeyInputHandler keyHandler;
	private Torus myTorus;
	private int mountainNormalMap, moonTexture;
	private int numTorusVertices, numTorusIndices;
	private float cameraX, cameraY, cameraZ;
	private Vector3f cameraU = new Vector3f();
	private Vector3f cameraV = new Vector3f();
	private Vector3f cameraN = new Vector3f();
	public Sphere mySphere;
	public ImportedModel grass, cabin;
	private float lightPosX, lightPosY, lightPosZ;

	private Vector3f initialLightLoc = new Vector3f(5.0f, 2.0f, 2.0f);
	private float amt = 0.0f;
	private double prevTime;
	private double elapsedTime;

	// allocate variables for display() function
	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);
	private Matrix4fStack mvStack = new Matrix4fStack(5);
	private Matrix4f pMat = new Matrix4f(); // perspective matrix
	private Matrix4f vMat = new Matrix4f(); // view matrix
	private Matrix4f mMat = new Matrix4f(); // model matrix
	private Matrix4f invTrMat = new Matrix4f(); // inverse-transpose
	private int mLoc, vLoc, pLoc, nLoc;
	private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;
	private float aspect;
	private int skyboxTexture, mountainTexture, grassTexure;
	private Vector3f currentLightPos = new Vector3f();
	private float[] lightPos = new float[3];

	// white light properties
	float[] globalAmbient = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float[] lightAmbient = new float[] { 0.1f, 0.1f, 0.1f, 1.0f };
	float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
	float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	// gold material
	float[] matAmb = Utils.goldAmbient();
	float[] matDif = Utils.goldDiffuse();
	float[] matSpe = Utils.goldSpecular();
	float matShi = Utils.goldShininess();

	public Code() {
		setTitle("A3- Lighting - Jonathon Delemos");
		setSize(1000, 1000);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		this.add(myCanvas);
		this.setVisible(true);
		Animator animator = new Animator(myCanvas);
		animator.start();
	}

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		displayHandler.display(gl, aspect);
	}

	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		lightPosX = 0.0f;
		lightPosY = 10.0f;
		lightPosZ = -18.0f;
		// https://www.cgtrader.com/free-3d-models/exterior/landscape/grass-c1bcf44d-1347-40cc-9bd6-b4399dc6d24b
		// this is the grass you see
		grass = new ImportedModel("grass.obj");
		// https://www.cgtrader.com/items/4659959/download-page log cabin
		cabin = new ImportedModel("cabin.obj");
		// https://freepngimg.com/png/82984-skybox-blue-atmosphere-sky-space-hd-image-free-png
		skyboxTexture = Utils.loadTexture("sky.png");
		// https://www.cgtrader.com/free-3d-models/exterior/landscape/grass-c1bcf44d-1347-40cc-9bd6-b4399dc6d24b
		mountainTexture = Utils.loadTexture("mountain.png");
		grassTexure = Utils.loadTexture("grass.jpg");
		// https://svs.gsfc.nasa.gov/4720/
		moonTexture = Utils.loadTexture("moon.jpg");

		// https://www.cgtrader.com/items/4659959/download-page log cabin
		mountainNormalMap = 1;

		VertexSetup vertexSetup = new VertexSetup(vao, vbo);
		vertexSetup.setupVertices(gl, vao, vbo, myTorus, grass, cabin);
		skyboxProgram = Utils.createShaderProgram("a3/skyboxVert.glsl", "a3/skyboxFrag.glsl");
		normalMapProgram = Utils.createShaderProgram("a3/normalMapVert.glsl", "a3/normalMapFrag.glsl");
		renderingProgram = Utils.createShaderProgram("a3/vertShader.glsl", "a3/fragShader.glsl");
		numTorusIndices = vertexSetup.getNumTorusIndices();
		mySphere = new Sphere();
		prevTime = System.currentTimeMillis();
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat = new Matrix4f().perspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

		cameraX = 0.0f;
		cameraY = 2.0f;
		cameraZ = 1.0f;
		cameraU.set(1.0f, 0.0f, 0.0f);
		cameraV.set(0.0f, 1.0f, 0.0f);
		cameraN.set(0.0f, 0.0f, -1.0f);

		displayHandler = new DisplayHandler(
				renderingProgram, skyboxProgram, normalMapProgram, vbo, numTorusIndices,
				initialLightLoc, skyboxTexture, moonTexture, grassTexure, mountainTexture, grass, cabin,
				cameraX, cameraY, cameraZ,
				cameraU, cameraV, cameraN, mountainNormalMap, mySphere, lightPosX, lightPosY, lightPosZ);

		keyHandler = new KeyInputHandler(displayHandler);
		myCanvas.addKeyListener(keyHandler);
		myCanvas.requestFocusInWindow();
	}

	public static void main(String[] args) {
		new Code();
	}

	public void dispose(GLAutoDrawable drawable) {
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
	}
}