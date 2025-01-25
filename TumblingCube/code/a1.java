
//need the package too it seems 
package a1;


import javax.swing.*;
import static com.jogamp.opengl.GL4.*; 
import com.jogamp.opengl.*; 
import com.jogamp.opengl.awt.GLCanvas; 
import java.io.File; 
import java.io.IOException; 
import java.util.Scanner;
import java.util.Vector; 
//Program 2.6 Animation addition 
import com.jogamp.opengl.util.*; 


public class Code extends JFrame implements GLEventListener

{	
	//Variable declarations 
	private GLCanvas myCanvas; 
	private int renderingProgram; 
	private int vao[] = new int[1]; 
	private float x = 0.0f; //location of the triangle
	private float inc = 0.01f; //offset for moving the triangle 

	//default constructor 
	public Code()
	{
		setTitle("Chapter 2 - Program 2.5 - Triangle - Jonathon Delemos");
		setSize(600,400); 
		setLocation(200,200); 
		myCanvas = new GLCanvas(); 
		myCanvas.addGLEventListener(this); 
		this.add(myCanvas); 
		this.setVisible(true);
		Animator animtr = new Animator(myCanvas); 
		animtr.start(); 
		


	}
	//end default constructor

	//overrides the display? 
	public void display(GLAutoDrawable drawable)
	{
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_DEPTH_BUFFER_BIT); 
		gl.glClear(GL_COLOR_BUFFER_BIT); 
		gl.glUseProgram(renderingProgram);
		x+= inc; 
		if (x > 1.0f) inc = -0.01f; 
		if (x < -1.0f) inc = 0.01f; 
		//contiue here
		int offsetLoc = gl.glGetUniformLocation(renderingProgram, "offset"); 
		gl.glProgramUniform1f(renderingProgram, offsetLoc, x); 
		gl.glDrawArrays(GL_TRIANGLES,0,3); 

	}



	//what happened here? It seems to take in a variable that is being declared in the parameter. odd 
	public void init(GLAutoDrawable drawable) 
	
	{
		GL4 gl = (GL4) GLContext.getCurrentGL(); 
		renderingProgram = createShaderProgram(); 
		gl.glGenVertexArrays(vao.length, vao, 0); 
		gl.glBindVertexArray(vao[0]);
  
	}
	// end init 

	private String[] readShaderSource(String filename) 
	{
		Vector<String> lines = new Vector<String>(); 
		Scanner sc; 
		String[] program; 

		try
		{
			sc = new Scanner(new File(filename));
			while (sc.hasNext())
			{
				lines.addElement(sc.nextLine());	
			}
			program = new String[lines.size()]; 

			for(int i = 0; i< lines.size(); i++) 
			{
				program[i] = (String) lines.elementAt(i) + "\n"; 
			}
		}

		catch (IOException e) 
		{
			System.err.println("IOException reading file: " + e);
			return null; 

		}
			return program; 
	}


	//createShaderProgram new function 2.2
	private int createShaderProgram() 
	{

		//declarations 
		GL4 gl = (GL4) GLContext.getCurrentGL(); 

		//hardcoding the vertex code, THIS MAKES PERFECT SENSE! 
		String vshaderSource[] = 
		{
			"#version 430  \n uniform float offset; \n",
			"void main(void)  \n",
			"{ if (gl_VertexID == 0) gl_Position = vec4(0.25 + offset, 0.-.25, 0.0, 1.0); else if (gl_VertexID == 1) gl_Position = vec4(-0.25 + offset,-0.25,0.0,1.0); else gl_Position = vec4(0.25 +offset, 0.25, 0.0, 1.0); }  \n",
		}; 
		
		//hardcoding the fragment shader code, this is how the pixels are colored 
		String fshaderSource[] = 
		{
			"#version 430  \n",
			"out vec4 color;   \n",
			"void main(void)    \n", 
			"{ color = vec4(0.0, 0.0, 1.0, 1.0); } \n",

		}; 

		//vshaderSource = readShaderSource(vert1Shader.glsl); 
		//fshaderSource = readShaderSource(frag1Shader.glsl); 

		//compiling the shaders  
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER); 
		gl.glShaderSource(vShader, 3, vshaderSource, null, 0); 
		gl.glCompileShader(vShader); 

		//compiling the fragments and colors
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, 4, fshaderSource, null, 0); 
		gl.glCompileShader(fShader); 

		int vfProgram = gl.glCreateProgram(); 
		gl.glAttachShader(vfProgram, vShader); 
		gl.glAttachShader(vfProgram, fShader); 
		gl.glLinkProgram(vfProgram); 

		gl.glDeleteShader(vShader); 
		gl.glDeleteShader(fShader);
		return vfProgram; 

	}




	

		//driver code
	public static void main( String[] args)
		{	
			//The constructor is creating the screen upon being instantiated 
			new Code(); 
	
		}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}


}// end commands 




















