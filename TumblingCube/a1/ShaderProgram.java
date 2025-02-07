/*
 * ---------------------------------------------------------
 * File: ShaderProgram.java
 * Author: Jonathon Delemos
 * University: California State University, Sacramento
 * Date: 2/1/2025
 * Assignment: Lab 1 - OpenGL and JOGL
 * ---------------------------------------------------------
 * Description:
 * This class is responsible for loading, compiling, and 
 * linking OpenGL shaders. It reads shader files, compiles 
 * them, checks for errors, and returns a valid shader program.
 * ---------------------------------------------------------
 */

package a1;

import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class ShaderProgram {

    //Function to create vertex and fragment shaders. This is being called by the main Code file
    public static int createShaderProgram() {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        // Read shader source files
        String[] vshaderSource = readShaderSource("a1/vert1Shader.glsl");
        String[] fshaderSource = readShaderSource("a1/frag1Shader.glsl");

        // Create and compile vertex shader
        int vertexShader = gl.glCreateShader(GL_VERTEX_SHADER);
        gl.glShaderSource(vertexShader, vshaderSource.length, vshaderSource, null, 0);
        gl.glCompileShader(vertexShader);
        checkShaderCompileError(gl, vertexShader, "Vertex Shader");

        // Create and compile fragment shader
        int fragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
        gl.glShaderSource(fragmentShader, fshaderSource.length, fshaderSource, null, 0);
        gl.glCompileShader(fragmentShader);
        checkShaderCompileError(gl, fragmentShader, "Fragment Shader");

        // Create and link shader program
        int vfProgram = gl.glCreateProgram();
        gl.glAttachShader(vfProgram, vertexShader);
        gl.glAttachShader(vfProgram, fragmentShader);
        gl.glLinkProgram(vfProgram);
        checkProgramLinkError(gl, vfProgram);

        // Delete shaders after linking
        gl.glDeleteShader(vertexShader);
        gl.glDeleteShader(fragmentShader);
        return vfProgram;
    }

    //function to read the shader source from the files in a1
    private static String[] readShaderSource(String filename) {
        Vector<String> lines = new Vector<>();
        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNextLine()) {
                lines.add(sc.nextLine().trim() + "\n");
            }
        } catch (IOException e) {
            System.err.println("IOException reading file: " + e.getMessage());
            return null;
        }
        return lines.toArray(new String[0]);
    }

    //checks for shader compiler erorr and prints the type of error to the console. 
    private static void checkShaderCompileError(GL4 gl, int shader, String shaderType) {
        int[] compiled = new int[1];
        gl.glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            System.err.println(shaderType + " compilation error.");
            System.exit(1);
        }
    }

    //checks for link errors 
    private static void checkProgramLinkError(GL4 gl, int program) {
        int[] linked = new int[1];
        gl.glGetProgramiv(program, GL_LINK_STATUS, linked, 0);
        if (linked[0] == 0) {
            System.err.println("Program link error.");
            System.exit(1);
        }
    }
}
