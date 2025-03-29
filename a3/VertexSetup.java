
/*
 * ---------------------------------------------------------
 * File: VertexSetup.java
 * Author: Jonathon Delemos
 * University: California State University, Sacramento
 * Date: 2/21/2025
 * Assignment: Lab 2 - OpenGL and JOGL
 * ---------------------------------------------------------
 * Description:
 * This class handles the vertex creation process in OpenGL.
 * In this class we define the vertices for the vertex shader and 
 * fragment shader. All vertex/texture coordinates are created here.
 * ---------------------------------------------------------
 */

package a3;

import com.jogamp.common.nio.Buffers;

import java.lang.Math;
import java.io.*;
import java.nio.*;
import java.lang.Math;
import javax.swing.*;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_STATIC_DRAW;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.common.nio.Buffers;
import org.joml.*;

public class VertexSetup {
        private int[] vbo;
        private int[] vao;

        public Sphere mySphere;
        private int numSphereVerts;
        public int numObjVertices;
        private int numTorusIndices;
        private int numTorusVertices;
        public int numObjVertices3;
        int numMountainVertices;
        int numCabinVertices;
        public ImportedModel mountain;
        public ImportedModel waterModel;
        public ImportedModel cabinModel;
        private Torus myTorus;

        public VertexSetup(int[] vao, int[] vbo) {
                this.vao = vao;
                this.vbo = vbo;
        }

        public void setupVertices(GL4 gl, int[] vao, int[] vbo, Torus myTorus, ImportedModel mountain,
                        ImportedModel cabinModel) {
                gl = (GL4) GLContext.getCurrentGL();

                // cube

                float[] cubeVertexPositions = { -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
                                1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
                                1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
                                1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
                                1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                                -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
                                -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
                                -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
                                -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
                                1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
                                -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
                                1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
                };

                float[] cubeTextureCoord = { 1.00f, 0.6666666f, 1.00f, 0.3333333f, 0.75f, 0.3333333f, // back face lower
                                                                                                      // right
                                0.75f, 0.3333333f, 0.75f, 0.6666666f, 1.00f, 0.6666666f, // back face upper left
                                0.75f, 0.3333333f, 0.50f, 0.3333333f, 0.75f, 0.6666666f, // right face lower right
                                0.50f, 0.3333333f, 0.50f, 0.6666666f, 0.75f, 0.6666666f, // right face upper left
                                0.50f, 0.3333333f, 0.25f, 0.3333333f, 0.50f, 0.6666666f, // front face lower right
                                0.25f, 0.3333333f, 0.25f, 0.6666666f, 0.50f, 0.6666666f, // front face upper left
                                0.25f, 0.3333333f, 0.00f, 0.3333333f, 0.25f, 0.6666666f, // left face lower right
                                0.00f, 0.3333333f, 0.00f, 0.6666666f, 0.25f, 0.6666666f, // left face upper left
                                0.25f, 0.3333333f, 0.50f, 0.3333333f, 0.50f, 0.0000000f, // bottom face upper right
                                0.50f, 0.0000000f, 0.25f, 0.0000000f, 0.25f, 0.3333333f, // bottom face lower left
                                0.25f, 1.0000000f, 0.50f, 1.0000000f, 0.50f, 0.6666666f, // top face upper right
                                0.50f, 0.6666666f, 0.25f, 0.6666666f, 0.25f, 1.0000000f // top face lower left
                };

                numCabinVertices = cabinModel.getNumVertices();
                System.out.println(numCabinVertices);

                Vector3f[] cabinVertices = cabinModel.getVertices();
                Vector2f[] cabinTexCoords = cabinModel.getTexCoords();
                Vector3f[] cabinNormals = cabinModel.getNormals();

                float[] cabinPvalues = new float[cabinVertices.length * 3];
                float[] cabinTvalues = new float[cabinTexCoords.length * 2];
                float[] cabinNvalues = new float[cabinNormals.length * 3];

                for (int i = 0; i < numCabinVertices; i++) {
                        cabinPvalues[i * 3] = (float) cabinVertices[i].x();
                        cabinPvalues[i * 3 + 1] = (float) cabinVertices[i].y();
                        cabinPvalues[i * 3 + 2] = (float) cabinVertices[i].z();
                        cabinTvalues[i * 2] = (float) cabinTexCoords[i].x();
                        cabinTvalues[i * 2 + 1] = (float) cabinTexCoords[i].y();
                        cabinNvalues[i * 3] = (float) cabinNormals[i].x();
                        cabinNvalues[i * 3 + 1] = (float) cabinNormals[i].y();
                        cabinNvalues[i * 3 + 2] = (float) cabinNormals[i].z();
                }

                numMountainVertices = mountain.getNumVertices();
                System.out.println(numMountainVertices);

                Vector3f[] mountainVertices = mountain.getVertices();
                Vector2f[] mountainTexCoords = mountain.getTexCoords();
                Vector3f[] mountainNormals = mountain.getNormals();

                float[] mountainPvalues = new float[mountainVertices.length * 3];
                float[] mountainTvalues = new float[mountainTexCoords.length * 2];
                float[] mountainNvalues = new float[mountainNormals.length * 3];

                for (int i = 0; i < numMountainVertices; i++) {
                        mountainPvalues[i * 3] = (float) mountainVertices[i].x();
                        mountainPvalues[i * 3 + 1] = (float) mountainVertices[i].y();
                        mountainPvalues[i * 3 + 2] = (float) mountainVertices[i].z();
                        mountainTvalues[i * 2] = (float) mountainTexCoords[i].x();
                        mountainTvalues[i * 2 + 1] = (float) mountainTexCoords[i].y();
                        mountainNvalues[i * 3] = (float) mountainNormals[i].x();
                        mountainNvalues[i * 3 + 1] = (float) mountainNormals[i].y();
                        mountainNvalues[i * 3 + 2] = (float) mountainNormals[i].z();
                }

                myTorus = new Torus(0.5f, 0.2f, 48);
                numTorusVertices = myTorus.getNumVertices();
                numTorusIndices = myTorus.getNumIndices();

                Vector3f[] vertices = myTorus.getVertices();
                Vector2f[] texCoords = myTorus.getTexCoords();
                Vector3f[] normals = myTorus.getNormals();
                int[] indices = myTorus.getIndices();

                float[] pvalues = new float[vertices.length * 3];
                float[] tvalues = new float[texCoords.length * 2];
                float[] nvalues = new float[normals.length * 3];

                for (int i = 0; i < numTorusVertices; i++) {
                        pvalues[i * 3] = (float) vertices[i].x();
                        pvalues[i * 3 + 1] = (float) vertices[i].y();
                        pvalues[i * 3 + 2] = (float) vertices[i].z();
                        tvalues[i * 2] = (float) texCoords[i].x();
                        tvalues[i * 2 + 1] = (float) texCoords[i].y();
                        nvalues[i * 3] = (float) normals[i].x();
                        nvalues[i * 3 + 1] = (float) normals[i].y();
                        nvalues[i * 3 + 2] = (float) normals[i].z();
                }

                // sphere delcarations. Still trying to figure this stuff out
                mySphere = new Sphere();
                numSphereVerts = mySphere.getIndices().length;
                System.out.println(numSphereVerts);

                int[] indices2 = mySphere.getIndices();
                Vector3f[] vert2 = mySphere.getVertices();
                Vector2f[] tex2 = mySphere.getTexCoords();
                Vector3f[] norm2 = mySphere.getNormals();

                float[] pvalues2 = new float[indices2.length * 3];
                float[] tvalues2 = new float[indices2.length * 2];
                float[] nvalues2 = new float[indices2.length * 3];

                for (int i = 0; i < indices.length; i++) {
                        pvalues2[i * 3] = (float) (vert2[indices[i]]).x;
                        pvalues2[i * 3 + 1] = (float) (vert2[indices[i]]).y;
                        pvalues2[i * 3 + 2] = (float) (vert2[indices[i]]).z;
                        tvalues2[i * 2] = (float) (tex2[indices[i]]).x;
                        tvalues2[i * 2 + 1] = (float) (tex2[indices[i]]).y;
                        nvalues2[i * 3] = (float) (norm2[indices[i]]).x;
                        nvalues2[i * 3 + 1] = (float) (norm2[indices[i]]).y;
                        nvalues2[i * 3 + 2] = (float) (norm2[indices[i]]).z;
                }

                gl.glGenVertexArrays(vao.length, vao, 0);
                gl.glBindVertexArray(vao[0]);
                gl.glGenBuffers(15, vbo, 0);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
                FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
                FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
                FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit() * 4, norBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[3]);
                IntBuffer idxBuf = Buffers.newDirectIntBuffer(indices);
                gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, idxBuf.limit() * 4, idxBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
                FloatBuffer cvertBuf = Buffers.newDirectFloatBuffer(cubeVertexPositions);
                gl.glBufferData(GL_ARRAY_BUFFER, cvertBuf.limit() * 4, cvertBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
                FloatBuffer ctexBuf = Buffers.newDirectFloatBuffer(cubeTextureCoord);
                gl.glBufferData(GL_ARRAY_BUFFER, ctexBuf.limit() * 4, ctexBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
                FloatBuffer mountainVertBuf = Buffers.newDirectFloatBuffer(mountainPvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, mountainVertBuf.limit() * 4, mountainVertBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
                FloatBuffer mountainTexBuf = Buffers.newDirectFloatBuffer(mountainTvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, mountainTexBuf.limit() * 4, mountainTexBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
                FloatBuffer mountainNorBuf = Buffers.newDirectFloatBuffer(mountainNvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, mountainNorBuf.limit() * 4, mountainNorBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
                FloatBuffer cabinVertBuf = Buffers.newDirectFloatBuffer(cabinPvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, cabinVertBuf.limit() * 4, cabinVertBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
                FloatBuffer cabinTexBuf = Buffers.newDirectFloatBuffer(cabinTvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, cabinTexBuf.limit() * 4, cabinTexBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
                FloatBuffer cabinNorBuf = Buffers.newDirectFloatBuffer(cabinNvalues);
                gl.glBufferData(GL_ARRAY_BUFFER, cabinNorBuf.limit() * 4, cabinNorBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
                FloatBuffer sphereVertBuf = Buffers.newDirectFloatBuffer(pvalues2);
                gl.glBufferData(GL_ARRAY_BUFFER, sphereVertBuf.limit() * 4, sphereVertBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
                FloatBuffer sphereTexBuf = Buffers.newDirectFloatBuffer(tvalues2);
                gl.glBufferData(GL_ARRAY_BUFFER, sphereTexBuf.limit() * 4, sphereTexBuf, GL_STATIC_DRAW);

                gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
                FloatBuffer sphereNorBuf = Buffers.newDirectFloatBuffer(nvalues2);
                gl.glBufferData(GL_ARRAY_BUFFER, sphereNorBuf.limit() * 4, sphereNorBuf, GL_STATIC_DRAW);

        }

        public int getNumTorusIndices() {
                return numTorusIndices;
        }

        public int getNumTorusVertices() {
                return numTorusVertices;
        }

        public Torus getTorus() {
                return myTorus;
        }

}