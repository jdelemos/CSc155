
// /*
// * ---------------------------------------------------------
// * File: DisplayHandler.java
// * Author: Jonathon Delemos
// * University: California State University, Sacramento
// * Date: 2/21/2025
// * Assignment: Lab 3 - OpenGL and JOGL
// * ---------------------------------------------------------
// * Description:
// * This class handles the rendering process in OpenGL.
// * It updates transformations, manages matrix definitions,
// * and sends buffer data to the shaders.
// * ---------------------------------------------------------
// */

// package a3;

package a3;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import org.joml.Vector3f;

import org.joml.*;

import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_CULL_FACE;
import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_LEQUAL;
import static com.jogamp.opengl.GL.GL_REPEAT;
import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE1;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL4.*;

public class DisplayHandler {
    private int renderingProgram;
    private int skyboxProgram;
    private int mountainNormalMap;
    private int[] vbo;
    private int mLoc, vLoc, pLoc, nLoc;
    private FloatBuffer vals;
    private Matrix4fStack mvStack;
    private Matrix4f pMat, vMat, mMat, invTrMat;
    private int numTorusIndices;
    private Vector3f initialLightLoc;
    private Vector3f currentLightPos;
    private float amt = 0.0f;
    private double prevTime;
    private float[] lightPos = new float[3];
    private float[] lightPos2 = new float[3];
    private Vector3f cameraLoc;
    private int skyboxTexture, mountainTexture, grassTexture, moonTexture;
    private Matrix4f vMatSkybox;
    private DisplayHandler displayHandler;
    public ImportedModel mountain, cabin;
    public int normalMapProgram;
    public Sphere mySphere;
    public float lightPosX, lightPosY, lightPosZ;

    private float rotationAngle = 0.0f;

    private float[] globalAmbient = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
    private float[] lightAmbient = new float[] { 0.1f, 0.1f, 0.1f, 1.0f };
    private float[] lightDiffuse = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float[] lightSpecular = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

    float[] grassAmb = { 0.15f, 0.1f, 0.08f, 1.0f };
    float[] grassDiff = { 0.2f, 0.5f, 0.2f, 1.0f };
    float[] grassSpec = { 0.05f, 0.05f, 0.05f, 1.0f };
    float grassShiny = 5.0f;

    private float[] logAmb = { 0.25f, 0.15f, 0.05f, 1.0f };
    private float[] logDiff = { 0.4f, 0.26f, 0.13f, 1.0f };
    private float[] logSpec = { 0.1f, 0.1f, 0.1f, 1.0f };
    private float logShiny = 5.0f;

    float[] moonAmbient = { 0.2f, 0.2f, 0.2f, 1.0f };
    float[] moonDiffuse = { 0.6f, 0.6f, 0.6f, 1.0f };
    float[] moonSpecular = { 0.0f, 0.0f, 0.0f, 1.00f };
    float moonShininess = 5.0f;

    private float[] matAmb = Utils.goldAmbient();
    private float[] matDif = Utils.goldDiffuse();
    private float[] matSpe = Utils.goldSpecular();
    private float matShi = Utils.goldShininess();

    public float cameraX, cameraY, cameraZ;
    public float lightX, lightY, lightZ;
    private Vector3f cameraU = new Vector3f(1, 0, 0);
    private Vector3f cameraV = new Vector3f(0, 1, 0);
    private Vector3f cameraN = new Vector3f(0, 0, -1);

    private int globalAmbLoc, ambLoc, diffLoc, specLoc, posLoc, mambLoc, mdiffLoc, mspecLoc, mshiLoc;

    public DisplayHandler(int renderingProgram, int skyboxProgram, int normalMapProgram, int[] vbo, int numTorusIndices,
            Vector3f initialLightLoc, int skyboxTexture, int moonTexture, int grassTexture, int mountainTexture,
            ImportedModel mountain,
            ImportedModel cabin,
            float cameraX, float cameraY, float cameraZ,
            Vector3f cameraU, Vector3f cameraV, Vector3f cameraN, int mountainNormalMap, Sphere mySphere,
            float lightPosX, float lightPosY, float lightPosZ) {
        this.renderingProgram = renderingProgram;
        this.skyboxProgram = skyboxProgram;
        this.vbo = vbo;
        this.numTorusIndices = numTorusIndices;
        this.initialLightLoc = initialLightLoc;
        this.skyboxTexture = skyboxTexture;
        this.grassTexture = grassTexture;
        this.mountainTexture = mountainTexture;
        this.moonTexture = moonTexture;
        this.mountain = mountain;
        this.cabin = cabin;
        this.mySphere = mySphere;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
        this.normalMapProgram = normalMapProgram;
        this.mountainNormalMap = mountainNormalMap;
        this.cameraU.set(cameraU);
        this.cameraV.set(cameraV);
        this.cameraN.set(cameraN);
        this.lightPosX = lightPosX;
        this.lightPosY = lightPosY;
        this.lightPosZ = lightPosZ;

        this.mvStack = new Matrix4fStack(5);
        this.pMat = new Matrix4f();
        this.vMat = new Matrix4f();
        this.mMat = new Matrix4f();
        this.invTrMat = new Matrix4f();
        this.vals = Buffers.newDirectFloatBuffer(16);
        this.currentLightPos = new Vector3f();
        this.prevTime = System.currentTimeMillis();
    }

    public void display(GL4 gl, float aspect) {
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);

        // Update view and projection
        vMat = buildViewMatrix(cameraX, cameraY, cameraZ);
        pMat.identity().perspective((float) java.lang.Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);

        // === Skybox ===
        gl.glUseProgram(skyboxProgram);
        gl.glDepthMask(false);
        gl.glDepthFunc(GL4.GL_LEQUAL);
        drawSkyBox(gl);
        drawSphere(gl, 0, 6, -15, moonTexture);
        gl.glDepthMask(true);
        gl.glDepthFunc(GL4.GL_LESS);

        // === Torus ===
        gl.glUseProgram(renderingProgram);

        installLights(gl);
        installLights2(gl, lightPosX, lightPosY, lightPosZ);
        // here's my second light
        installSecondLight(gl,
                new float[] { 0.f, 4.0f, -5.0f },
                lightAmbient,
                lightDiffuse,
                lightSpecular);
        currentLightPos.set(initialLightLoc);
        double elapsedTime = System.currentTimeMillis() - prevTime;
        prevTime = System.currentTimeMillis();
        amt += elapsedTime * 0.03f;
        currentLightPos.rotateAxis((float) java.lang.Math.toRadians(amt), 0.0f, 0.0f, 1.0f);

        mLoc = gl.glGetUniformLocation(renderingProgram, "m_matrix");
        vLoc = gl.glGetUniformLocation(renderingProgram, "v_matrix");
        pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix");
        nLoc = gl.glGetUniformLocation(renderingProgram, "norm_matrix");
        // drawTorus(gl);
        gl.glUseProgram(renderingProgram);

        drawOBJ(gl, vbo[6], vbo[7], grassTexture, renderingProgram, 0f, 0.5f, 0f, grassAmb, grassDiff, grassSpec,
                grassShiny, mountain, vbo[8], 1);
        drawOBJ(gl, vbo[9], vbo[10], mountainTexture, renderingProgram, -7f, 1.0f, -7f, logAmb, logDiff, logSpec,
                logShiny, cabin, vbo[11], 0);

    }

    private void drawOBJ(GL4 gl, int vboID, int texCoordVBO, int textureID, int shaderProgram,
            float locX, float locY, float locZ, float[] ambient, float[] diffuse, float[] specular, float shininess,
            ImportedModel model, int vboNorms, int num) {
        gl.glUseProgram(shaderProgram);

        // Model matrix
        Matrix4f modelMat = new Matrix4f().translation(locX, locY, locZ);
        invTrMat.set(modelMat).invert().transpose();
        mvStack.pushMatrix();
        mvStack.mul(modelMat);
        mMat.set(mvStack);

        installMaterial(gl, shaderProgram, ambient, diffuse, specular, shininess);

        // Upload matrices
        gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
        gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
        gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

        // Bind vertex positions
        gl.glBindBuffer(GL_ARRAY_BUFFER, vboID);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // Bind texture coordinates
        gl.glBindBuffer(GL_ARRAY_BUFFER, vboNorms);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glBindBuffer(GL_ARRAY_BUFFER, texCoordVBO);
        gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0); // texCoord
        gl.glEnableVertexAttribArray(2);

        // Bind texture
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, textureID);
        if (num == 1) {
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        }

        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        // Draw the model
        gl.glDrawArrays(GL_TRIANGLES, 0, model.getNumVertices());

        mvStack.popMatrix();
    }

    private void drawSphere(GL4 gl, float locX, float locY, float locZ, int moonTexture) {
        gl.glUseProgram(renderingProgram);
        // === Planet (main sphere) ===
        mvStack.pushMatrix();
        mvStack.translate(locX, locY, locZ); // Move to orbit center (e.g. around sun)

        float angleSpeed = 0.15f;
        rotationAngle += angleSpeed;
        float radians = (float) java.lang.Math.toRadians(rotationAngle);

        // Planet rotates on its own axis
        mvStack.rotate(radians, 0.0f, 0.0f, 1.0f);

        // Draw planet
        mMat.set(mvStack);
        invTrMat.set(mMat).invert().transpose();

        installMaterial(gl, renderingProgram, moonAmbient, moonDiffuse, moonSpecular,
                moonShininess);

        gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
        gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
        gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
        gl.glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(2);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, moonTexture);
        gl.glDrawArrays(GL_TRIANGLES, 0, mySphere.getIndices().length);

        // === Moon ===
        mvStack.pushMatrix();
        mvStack.rotate(radians * 2.0f, 0.0f, 1.0f, 0.0f);
        mvStack.translate(2.0f, 0.0f, 0.0f);
        mvStack.scale(0.3f);

        mMat.set(mvStack);
        invTrMat.set(mMat).invert().transpose();
        gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
        gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
        gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

        gl.glDrawArrays(GL_TRIANGLES, 0, mySphere.getIndices().length);

        mvStack.popMatrix(); // Pop moon
        mvStack.popMatrix(); // Pop planet
    }

    private void drawTorus(GL4 gl) {
        gl.glUseProgram(renderingProgram);

        mvStack.pushMatrix();
        mvStack.translate(0.0f, 1.0f, -3.0f);
        mMat.set(mvStack); // Model matrix from stack

        invTrMat.set(mMat).invert().transpose();

        gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
        gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
        gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[3]);
        gl.glDrawElements(GL_TRIANGLES, numTorusIndices, GL_UNSIGNED_INT, 0);

        mvStack.popMatrix();
    }

    private void installLights(GL4 gl) {
        lightPos2[0] = currentLightPos.x();
        lightPos2[1] = currentLightPos.y();
        lightPos2[2] = currentLightPos.z();

        globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
        ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
        diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
        specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
        posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
        mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
        mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
        mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
        mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");

        gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);
        gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
        gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
        gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
        gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos2, 0);
        gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, matAmb, 0);
        gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, matDif, 0);
        gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, matSpe, 0);
        gl.glProgramUniform1f(renderingProgram, mshiLoc, matShi);
    }

    private void installLights2(GL4 gl, float x, float y, float z) {
        lightPos[0] = x;
        lightPos[1] = y;
        lightPos[2] = z;

        globalAmbLoc = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
        ambLoc = gl.glGetUniformLocation(renderingProgram, "light.ambient");
        diffLoc = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
        specLoc = gl.glGetUniformLocation(renderingProgram, "light.specular");
        posLoc = gl.glGetUniformLocation(renderingProgram, "light.position");
        mambLoc = gl.glGetUniformLocation(renderingProgram, "material.ambient");
        mdiffLoc = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
        mspecLoc = gl.glGetUniformLocation(renderingProgram, "material.specular");
        mshiLoc = gl.glGetUniformLocation(renderingProgram, "material.shininess");

        gl.glProgramUniform4fv(renderingProgram, globalAmbLoc, 1, globalAmbient, 0);
        gl.glProgramUniform4fv(renderingProgram, ambLoc, 1, lightAmbient, 0);
        gl.glProgramUniform4fv(renderingProgram, diffLoc, 1, lightDiffuse, 0);
        gl.glProgramUniform4fv(renderingProgram, specLoc, 1, lightSpecular, 0);
        gl.glProgramUniform3fv(renderingProgram, posLoc, 1, lightPos, 0);
        gl.glProgramUniform4fv(renderingProgram, mambLoc, 1, matAmb, 0);
        gl.glProgramUniform4fv(renderingProgram, mdiffLoc, 1, matDif, 0);
        gl.glProgramUniform4fv(renderingProgram, mspecLoc, 1, matSpe, 0);
        gl.glProgramUniform1f(renderingProgram, mshiLoc, matShi);
    }

    private void installSecondLight(GL4 gl, float[] pos, float[] ambient, float[] diffuse, float[] specular) {
        int ambLoc2 = gl.glGetUniformLocation(renderingProgram, "light2.ambient");
        int diffLoc2 = gl.glGetUniformLocation(renderingProgram, "light2.diffuse");
        int specLoc2 = gl.glGetUniformLocation(renderingProgram, "light2.specular");
        int posLoc2 = gl.glGetUniformLocation(renderingProgram, "light2.position");

        gl.glProgramUniform4fv(renderingProgram, ambLoc2, 1, ambient, 0);
        gl.glProgramUniform4fv(renderingProgram, diffLoc2, 1, diffuse, 0);
        gl.glProgramUniform4fv(renderingProgram, specLoc2, 1, specular, 0);
        gl.glProgramUniform3fv(renderingProgram, posLoc2, 1, pos, 0);
    }

    private void installMaterial(GL4 gl, int shaderProgram,
            float[] ambient, float[] diffuse, float[] specular, float shininess) {
        int mambLoc = gl.glGetUniformLocation(shaderProgram, "material.ambient");
        int mdiffLoc = gl.glGetUniformLocation(shaderProgram, "material.diffuse");
        int mspecLoc = gl.glGetUniformLocation(shaderProgram, "material.specular");
        int mshiLoc = gl.glGetUniformLocation(shaderProgram, "material.shininess");

        gl.glProgramUniform4fv(shaderProgram, mambLoc, 1, ambient, 0);
        gl.glProgramUniform4fv(shaderProgram, mdiffLoc, 1, diffuse, 0);
        gl.glProgramUniform4fv(shaderProgram, mspecLoc, 1, specular, 0);
        gl.glProgramUniform1f(shaderProgram, mshiLoc, shininess);
    }

    private void drawSkyBox(GL4 gl) {
        gl.glDepthMask(false);
        gl.glDisable(GL4.GL_DEPTH_TEST);

        // Strip translation from view matrix for skybox
        Matrix4f skyboxView = new Matrix4f(vMat);
        skyboxView.m30(0).m31(0).m32(0);

        int mvLoc = gl.glGetUniformLocation(skyboxProgram, "mv_matrix");
        int pLoc = gl.glGetUniformLocation(skyboxProgram, "p_matrix");
        int texLoc = gl.glGetUniformLocation(skyboxProgram, "tex");

        gl.glUniformMatrix4fv(mvLoc, 1, false, skyboxView.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
        gl.glUniform1i(texLoc, 0);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, skyboxTexture);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 36);

        gl.glDepthMask(true);
        gl.glEnable(GL4.GL_DEPTH_TEST);
    }

    public Matrix4f buildViewMatrix(float ux, float uy, float uz) {
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.set(
                cameraU.get(0), cameraV.get(0), -cameraN.get(0), 0,
                cameraU.get(1), cameraV.get(1), -cameraN.get(1), 0,
                cameraU.get(2), cameraV.get(2), -cameraN.get(2), 0,
                0, 0, 0, 1);

        Matrix4f translationMatrix = new Matrix4f().translation(-cameraX, -cameraY, -cameraZ);

        Matrix4f newVMat = new Matrix4f();
        newVMat.identity();
        newVMat.mul(rotationMatrix).mul(translationMatrix);
        return newVMat;
    }

    public void moveBackward(float speed) {
        this.cameraX -= cameraN.get(0) * speed; // Move along -N
        this.cameraY -= cameraN.get(1) * speed;
        this.cameraZ -= cameraN.get(2) * speed;
        System.out.println("Object Reference: " + this);

    }

    public void moveLeft(float speed) {
        this.cameraX -= cameraU.get(0) * speed; // Move along -N
        this.cameraY -= cameraU.get(1) * speed;
        this.cameraZ -= cameraU.get(2) * speed;
        System.out.println("Object Reference: " + this);

    }

    public void moveRight(float speed) {
        this.cameraX += cameraU.get(0) * speed; // Move along -N
        this.cameraY += cameraU.get(1) * speed;
        this.cameraZ += cameraU.get(2) * speed;
        System.out.println("Object Reference: " + this);

    }

    public void moveForward(float speed) {
        this.cameraX += cameraN.get(0) * speed; // Move along +N
        this.cameraY += cameraN.get(1) * speed;
        this.cameraZ += cameraN.get(2) * speed;
    }

    public void moveUp(float speed) {
        cameraX += cameraV.get(0) * speed;
        cameraY += cameraV.get(1) * speed;
        cameraZ += cameraV.get(2) * speed;
    }

    public void moveDown(float speed) {
        cameraX -= cameraV.get(0) * speed;
        cameraY -= cameraV.get(1) * speed;
        cameraZ -= cameraV.get(2) * speed;
    }

    public void pan(float angle) {
        // 1. Rotate cameraN and cameraU around cameraV
        cameraU.rotateAxis(angle, cameraV.x, cameraV.y, cameraV.z);
        cameraN.rotateAxis(angle, cameraV.x, cameraV.y, cameraV.z);

    }

    // Pitch: Rotate cameraN and cameraV around the right vector (cameraU)
    public void pitch(float angle) {
        // Create rotation matrix around the RIGHT axis (cameraU)
        Matrix4f rotationMatrix = new Matrix4f().rotate(angle, this.cameraU);

        // Apply rotation to cameraN (forward) and cameraV (up)
        this.cameraN = rotationMatrix.transformDirection(this.cameraN);
        this.cameraV = rotationMatrix.transformDirection(this.cameraV);
    }

    public void setWorldAxes(float value) {
        /* toggle axis lines */ }

    public Vector3f getCameraN() {
        return new Vector3f(cameraN);
    }

    public void drawGround(GL4 gl, int groundVBO, int groundTexVBO, int groundTexture) {
        /* implement */ }

    public void drawSky(GL4 gl, int skyVBO, int skyTexVBO, int skyTexture) {
        /* implement */ }

    public void setCameraLoc(Vector3f newCamLoc) {
        this.cameraLoc.set(newCamLoc); // copy values in
    }

    public void setLightX() {
        this.lightPosX += 1.0f;
        System.out.println("Light X: " + this.lightPosX);
    }

    public void setLightNegX() {
        this.lightPosX -= 1.0f;
        System.out.println("Light X: " + this.lightPosX);
    }

    public void setLightZ() {
        this.lightPosZ += 1.0f;
        System.out.println("Light Y: " + this.lightPosZ);
    }

    public void setLightNegZ() {
        this.lightPosZ -= 1.0f;
        System.out.println("Light Y: " + this.lightPosZ);
    }

}

// /*
// * ---------------------------------------------------------
// * File: DisplayHandler.java
// * Author: Jonathon Delemos
// * University: California State University, Sacramento
// * Date: 2/21/2025
// * Assignment: Lab 2 - OpenGL and JOGL
// * ---------------------------------------------------------
// * Description:
// * This class handles the rendering process in OpenGL.
// * It updates transformations, manages matrix definitions,
// * and sends buffer data to the shaders.
// * ---------------------------------------------------------
// */

// package a3;

// import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
// import static com.jogamp.opengl.GL.GL_BACK;
// import static com.jogamp.opengl.GL.GL_CCW;
// import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;
// import static com.jogamp.opengl.GL.GL_CULL_FACE;
// import static com.jogamp.opengl.GL.GL_DEPTH_BUFFER_BIT;
// import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
// import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
// import static com.jogamp.opengl.GL.GL_FLOAT;
// import static com.jogamp.opengl.GL.GL_LEQUAL;
// import static com.jogamp.opengl.GL.GL_REPEAT;
// import static com.jogamp.opengl.GL.GL_TEXTURE0;
// import static com.jogamp.opengl.GL.GL_TEXTURE_2D;
// import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_S;
// import static com.jogamp.opengl.GL.GL_TEXTURE_WRAP_T;
// import static com.jogamp.opengl.GL.GL_TRIANGLES;
// import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;

// import java.nio.FloatBuffer;
// import com.jogamp.common.nio.Buffers;
// import com.jogamp.opengl.GL4;
// import static com.jogamp.opengl.GL4.*;
// import org.joml.Vector3f;
// import java.util.Random;
// import java.io.*;
// import java.lang.Math;
// import java.nio.*;
// import javax.swing.*;
// import com.jogamp.opengl.*;
// import com.jogamp.opengl.awt.GLCanvas;
// import com.jogamp.opengl.math.Matrix4f;
// import com.jogamp.opengl.util.texture.*;
// import com.jogamp.common.nio.Buffers;
// import org.joml.*;

// public class DisplayHandler {
// private int renderingProgram;
// private int[] vbo;
// float cloudx = 0;
// float cloudy = -8.0f;
// float cloudz = -15.0f;
// private int mvLoc, pLoc;
// private Matrix4f pMat, vMat, mMat, mvMat, rotationMatrix;
// private FloatBuffer vals;
// public float cameraX;
// public float cameraY;
// public float cameraZ;
// public float lineBit = 0.0f;
// private int pyramidTexture, ellipseTexture, skyTexture, moonTexture,
// cloudTexture, redTextureColor, greenTextureColor, blueTextureColor,
// houseTextureColor, sphinxTextureColor, obeliskTexture;

// int sand;
// private Vector3f cameraU = new Vector3f();
// private Vector3f cameraV = new Vector3f();
// private Vector3f cameraN = new Vector3f();
// private com.jogamp.opengl.math.Matrix4f tMatrix, rMatrix;
// private float speed = 0.1f;
// private float rotationSpeed = 0.05f;
// private float yaw = 0.0f;
// private float pitch = 0.0f;
// private final Vector3f worldUp = new Vector3f(0, 1, 0);
// private ImportedModel myModel;
// private ImportedModel waterModel;
// private Sphere mySphere;
// private float z = 3;
// private float houseLocation = 0.0f;
// private float rotationAngle = 0.0f;
// private long previousTime;

// // constructor
// public DisplayHandler(int renderingProgram, int[] vbo, int mvLoc, int pLoc,
// Matrix4f pMat, Matrix4f vMat,
// Matrix4f mMat, Matrix4f mvMat, FloatBuffer vals, int pyramidTexture, int
// ellipseTexture, int skyTexture,
// int moonTexture, int cloudTexture, float cameraX, float cameraY, float
// cameraZ, Vector3f cameraU,
// Vector3f cameraV, Vector3f cameraN, int redTextureColor, int
// greenTextureColor, int blueTextureColor,
// ImportedModel myModel, int sphinxTextureColor, Sphere mySphere, ImportedModel
// waterModel,
// int houseTextureColor, int obeliskTexture) {
// this.renderingProgram = renderingProgram;
// this.vbo = vbo;
// this.mvLoc = mvLoc;
// this.pLoc = pLoc;
// this.pMat = pMat;
// this.vMat = vMat;
// this.mMat = mMat;
// this.mvMat = mvMat;
// this.vals = vals;
// this.pyramidTexture = pyramidTexture;
// this.ellipseTexture = ellipseTexture;
// this.skyTexture = skyTexture;
// this.moonTexture = moonTexture;
// this.cloudTexture = cloudTexture;
// this.redTextureColor = redTextureColor;
// this.greenTextureColor = greenTextureColor;
// this.blueTextureColor = blueTextureColor;
// this.cameraX = cameraX;
// this.cameraY = cameraY;
// this.cameraZ = cameraZ;
// this.cameraU = cameraU;
// this.cameraV = cameraV;
// this.cameraN = cameraN;
// this.myModel = myModel;
// this.sphinxTextureColor = sphinxTextureColor;
// this.mySphere = mySphere;
// this.waterModel = waterModel;
// this.houseTextureColor = houseTextureColor;
// this.obeliskTexture = obeliskTexture;
// this.previousTime = System.nanoTime();

// }

// // display function
// public void display(GL4 gl, float cameraX, float cameraY, float cameraZ,
// float aspect, double circleAngle) {
// gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
// gl.glUseProgram(renderingProgram);

// // time requirement
// long currentTime = System.nanoTime();
// long deltaTime = currentTime - previousTime; // Time difference in
// nanoseconds
// double ELAPSEDTime = deltaTime / 1_000_000_000.0; // Convert to seconds
// previousTime = currentTime; // Update previous time for next frame
// double fps = 1_000_000_000.0 / deltaTime; // Convert to FPS

// pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
// // System.out.println("Camera Position: (" + cameraX + ", " + cameraY + ", "
// +
// // cameraZ + ")");

// vMat = buildViewMatrix(0, 0, -8.0f);
// // Print View Matrix (vMat) for Debugging SUPER USEFUL
// float[] debugVals = new float[16];
// vMat.get(debugVals);

// System.out.println("View Matrix (vMat):");
// for (int i = 0; i < 4; i++) {
// System.out.printf("| %6.2f %6.2f %6.2f %6.2f |\n",
// debugVals[i], debugVals[i + 4], debugVals[i + 8], debugVals[i + 12]);
// }

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, pyramidTexture);
// gl.glEnable(GL4.GL_DEPTH_TEST);
// gl.glDepthFunc(GL4.GL_LEQUAL);

// // cloud movement
// cloudx += ELAPSEDTime;
// if (cloudx > 14.0f) {
// cloudx = -14.0f;
// }
// // cloud movement
// cloudy += ELAPSEDTime;
// if (cloudy > 15.0f) {
// cloudy = -9.0f;
// }

// // cloud movement
// cloudz += ELAPSEDTime;
// if (cloudz > 15.0f) {
// cloudz = -9.0f;
// }

// // moon movement
// circleAngle += ELAPSEDTime;
// float x = 5.0f * (float) Math.cos(circleAngle);
// float y = 5.0f * (float) Math.sin(circleAngle);
// System.out.println(mySphere.getNumVertices());
// System.out.println(mySphere.getNumIndices());
// drawObject(gl, vbo[0], vbo[2], pyramidTexture, -2.0f, -1.0f, 0.0f, 18);
// drawObject(gl, vbo[0], vbo[2], pyramidTexture, 2.7f, -1.0f, -2.0f, 18);
// drawObject(gl, vbo[0], vbo[2], pyramidTexture, .5f, -1.0f, -1.0f, 18);
// drawObelisk(gl, -2.0f, -1.0f, 3.0f, vbo[18], vbo[19], obeliskTexture);

// // drawCircle(gl, vbo[6], moonTexture, vbo[7], x, y, -.10f);
// drawGround(gl, vbo[3], vbo[4], ellipseTexture);
// drawSky(gl, vbo[5], vbo[1], skyTexture);
// drawSphere(gl, x, y, -5);
// drawOBJ(gl, vbo[12], vbo[13], sphinxTextureColor, 2f, -2f, 3f);

// int rows = 20;
// int cols = 10;
// float xStart = -6f;
// float zStart = -2f;
// float spacing = 0.20f;

// for (int i = 0; i < rows; i++) {
// for (int j = 0; j < cols; j++) {
// float xPos = xStart + (j * spacing);
// float zPos = zStart + (i * spacing);
// drawOBJ(gl, vbo[6], vbo[7], houseTextureColor, xPos, -2, zPos);
// }
// }

// if (lineBit == 1.0f) {

// drawLine(gl, vbo[9], ellipseTexture, redTextureColor);
// drawLine(gl, vbo[10], ellipseTexture, greenTextureColor);
// drawLine(gl, vbo[11], ellipseTexture, blueTextureColor);

// }

// for (int i = 0; i < 25; i++) {
// if (i % 3 == 0) {
// drawCloud(gl, (cloudx + 0.6f) + i, 3.5f, i * 2);
// drawCloud(gl, (cloudx) + i, 4.0f, i * 2);
// drawCloud(gl, (cloudx - 0.6f) + i, 3.5f, i * 2);
// }
// if (i % 4 == 0) {
// drawCloud(gl, (cloudx + 0.6f) - i, 3.5f, i * 2);
// drawCloud(gl, (cloudx) - i, 4.0f, i * 2);
// drawCloud(gl, (cloudx - 0.6f) - i, 3.5f, i * 2);
// }
// if (i % 2 == 0) {
// drawCloud(gl, (cloudx + 0.6f) - i, 3.5f, i * -5);
// drawCloud(gl, (cloudx) - i, 4.0f, i * -5);
// drawCloud(gl, (cloudx - 0.6f) - i, 3.5f, i * -5);

// }

// }
// for (int i = 0; i < 25; i++) {
// if (i % 3 == 0) {
// drawCloud(gl, (cloudy + 0.6f) + i, 3.7f, i * 2);
// drawCloud(gl, (cloudy) + i, 4.0f, i * 2);
// drawCloud(gl, (cloudy - 0.6f) + i, 3.7f, i * 2);
// }
// if (i % 4 == 0) {
// drawCloud(gl, (cloudy + 0.6f) - i, 3.7f, i * 2);
// drawCloud(gl, (cloudy) - i, 4.0f, i * 2);
// drawCloud(gl, (cloudy - 0.6f) - i, 3.7f, i * 2);
// }
// if (i % 2 == 0) {
// drawCloud(gl, (cloudy + 0.6f) - i, 3.7f, i * -5);
// drawCloud(gl, (cloudy) - i, 4.0f, i * -5);
// drawCloud(gl, (cloudy - 0.6f) - i, 3.7f, i * -5);

// }

// }

// for (int i = 0; i < 25; i++) {
// if (i % 3 == 0) {
// drawCloud(gl, (cloudz + 0.6f) + i, 3.7f, i * 2);
// drawCloud(gl, (cloudz) + i, 4.0f, i * 2);
// drawCloud(gl, (cloudz - 0.6f) + i, 3.7f, i * 2);
// }
// if (i % 4 == 0) {
// drawCloud(gl, (cloudz + 0.6f) - i, 3.7f, i * 2);
// drawCloud(gl, (cloudz) - i, 4.0f, i * 2);
// drawCloud(gl, (cloudz - 0.6f) - i, 3.7f, i * 2);
// }
// if (i % 2 == 0) {
// drawCloud(gl, (cloudz + 0.6f) - i, 3.7f, i * -5);
// drawCloud(gl, (cloudz) - i, 4.0f, i * -5);
// drawCloud(gl, (cloudz - 0.6f) - i, 3.7f, i * -5);

// }

// }

// // since the matrices are being updated all the time, we need to bind them.
// mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix"); // View
// Matrix
// pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix"); // Projection
// Matrix

// // Upload View Matrix (mv_matrix)
// gl.glUniformMatrix4fv(mvLoc, 1, false, vMat.get(vals));
// // Upload Projection Matrix (p_matrix)
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// }

// // draw object functions. These are are basically the same and modelled from
// the
// // book
// private void drawObject(GL4 gl, int vboID, int textureCoord, int texure,
// float locX, float locY, float locZ,
// int numtrianlges) {
// mMat.identity();
// mMat.translate(locX, locY, locZ);

// mvMat.identity();
// mvMat.mul(vMat);
// mvMat.mul(mMat);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, pyramidTexture);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vboID);
// gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, textureCoord);
// gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glDrawArrays(GL4.GL_TRIANGLES, 0, numtrianlges);
// }

// private void drawTorus(GL4 gl) {
// mvStack.pushMatrix();
// mvStack.rotate((float) Math.toRadians(35.0f), 1.0f, 0.0f, 0.0f);

// mvStack.invert(invTrMat);
// mvStack.pushMatrix();
// mvStack.transpose(invTrMat);

// currentLightPos.set(initialLightLoc);
// double elapsedTime = System.currentTimeMillis() - prevTime;
// prevTime = System.currentTimeMillis();
// amt += elapsedTime * 0.03f;
// currentLightPos.rotateAxis((float) Math.toRadians(amt), 0.0f, 0.0f, 1.0f);

// installLights(gl);

// gl.glUniformMatrix4fv(mLoc, 1, false, mMat.get(vals));
// gl.glUniformMatrix4fv(vLoc, 1, false, vMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));
// gl.glUniformMatrix4fv(nLoc, 1, false, invTrMat.get(vals));

// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
// gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
// gl.glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glEnable(GL_CULL_FACE);
// gl.glFrontFace(GL_CCW);
// gl.glEnable(GL_DEPTH_TEST);
// gl.glDepthFunc(GL_LEQUAL);

// gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo[3]);
// gl.glDrawElements(GL_TRIANGLES, numTorusIndices, GL_UNSIGNED_INT, 0);

// mvStack.popMatrix();
// mvStack.popMatrix();
// }

// private void drawObelisk(GL4 gl, float locX, float locY, float locZ, int
// positionVBO, int texCoordVBO,
// int textureID) {
// mMat.identity();
// mMat.translate(locX, locY, locZ);
// mvMat.identity();
// mvMat.mul(vMat);
// mvMat.mul(mMat);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, textureID);

// gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
// gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
// gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL_ARRAY_BUFFER, texCoordVBO);
// gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glDrawArrays(GL_TRIANGLES, 0, 50);
// }

// private void drawObject2(GL4 gl, int vboID, int textureCoord, int texure,
// float locX, float locY, float locZ,
// int numtrianlges) {
// mMat.identity();
// mMat.translate(locX, locY, locZ);

// mvMat.identity();
// mvMat.mul(vMat);
// mvMat.mul(mMat);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, pyramidTexture);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[18]);
// gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[19]);
// gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glDrawArrays(GL4.GL_TRIANGLES, 0, numtrianlges);
// }

// // draw object functions. These are are basically the same.
// private void drawOBJ(GL4 gl, int vboID, int textureCoord, int textureColor,
// float locX, float locY, float locZ) {
// mMat.identity();
// mMat.translate(locX, locY, locZ);

// mvMat.identity();
// mvMat.mul(vMat);
// mvMat.mul(mMat);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL_ARRAY_BUFFER, vboID);
// gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL_ARRAY_BUFFER, textureCoord);
// gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, textureColor);

// gl.glEnable(GL_DEPTH_TEST);
// gl.glDepthFunc(GL_LEQUAL);
// gl.glDrawArrays(GL_TRIANGLES, 0, myModel.getNumVertices());
// }

// // draw object functions. These are are basically the same.
// private void drawCloud(GL4 gl, float locX, float locY, float locZ) {
// mMat.identity();
// mMat.scale(0.5f);
// mMat.translate(locX, locY, locZ);

// mvMat.identity();
// mvMat.mul(vMat);
// mvMat.mul(mMat);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
// gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
// gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, cloudTexture);

// gl.glDrawArrays(GL_TRIANGLES, 0, mySphere.getIndices().length);
// }

// private void drawGround(GL4 gl, int groundVBO, int groundTexVBO, int
// groundTexture) {
// mMat.identity();
// mMat.translate(0.0f, 0.0f, 0.0f);

// mvMat.identity();
// mvMat.mul(vMat);
// mvMat.mul(mMat);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, groundTexture);
// // Tiling
// gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
// gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundVBO);
// gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundTexVBO);
// gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 6);
// }

// private void drawLine(GL4 gl, int LineVBO, int LineTexVBO, int groundTexture)
// {
// mMat.identity();
// mMat.translate(0.0f, 0.0f, 0.0f); // Place it at the default position

// mvMat.identity();
// mvMat.mul(vMat); // Apply camera transformation
// mvMat.mul(mMat); // Apply model transformation

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, groundTexture);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, LineVBO);
// gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, LineTexVBO);
// gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);
// gl.glLineWidth(5.0f); // Set the desired line width s

// gl.glDrawArrays(GL4.GL_LINES, 0, 2);
// }

// private void drawSphere(GL4 gl, float locX, float locY, float locZ) {
// mMat.identity().translate(locX, locY, locZ);

// float angleSpeed = 0.15f;
// rotationAngle += angleSpeed; // if we just add it to itself, it will double.
// float radians = (float) Math.toRadians(rotationAngle);

// float cos = (float) Math.cos(radians);
// float sin = (float) Math.sin(radians);

// Matrix4f rotationMatrix = new Matrix4f();
// rotationMatrix.set(0, 0, cos);
// rotationMatrix.set(0, 1, -sin);
// rotationMatrix.set(1, 0, sin);
// rotationMatrix.set(1, 1, cos);
// rotationMatrix.set(2, 2, 1.0f);
// rotationMatrix.set(3, 3, 1.0f);

// mMat.mul(rotationMatrix);

// mvMat.identity().mul(vMat).mul(mMat);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
// gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
// gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, moonTexture);

// gl.glDrawArrays(GL_TRIANGLES, 0, mySphere.getIndices().length);
// }

// private void drawSky(GL4 gl, int groundVBO, int groundTexVBO, int
// groundTexture) {
// mMat.identity();
// mMat.translate(0.0f, 0.0f, 0.0f);

// mvMat.identity();
// mvMat.mul(vMat);
// mvMat.mul(mMat);

// gl.glActiveTexture(GL4.GL_TEXTURE0);
// gl.glBindTexture(GL4.GL_TEXTURE_2D, groundTexture);

// gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
// gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundVBO);
// gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(0);

// gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundTexVBO);
// gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
// gl.glEnableVertexAttribArray(1);

// gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 6);
// }

// // ---------------------Camera
// // functions-----------------------------------------//

// public Matrix4f buildViewMatrix(float ux, float uy, float uz) {
// // Construct the new rotation matrix (R)
// Matrix4f rotationMatrix = new Matrix4f();
// rotationMatrix.set(
// cameraU.get(0), cameraV.get(0), -cameraN.get(0), 0,
// cameraU.get(1), cameraV.get(1), -cameraN.get(1), 0,
// cameraU.get(2), cameraV.get(2), -cameraN.get(2), 0,
// 0, 0, 0, 1);

// // Construct the new translation matrix (T)
// Matrix4f translationMatrix = new Matrix4f();
// translationMatrix.identity();
// translationMatrix.set(3, 0, -cameraX); // -Cx
// translationMatrix.set(3, 1, -cameraY); // -Cy
// translationMatrix.set(3, 2, -cameraZ); // -Cz
// translationMatrix.set(3, 3, 1);

// // Calculation
// Matrix4f newVMat = new Matrix4f();
// newVMat.identity();
// newVMat.mul(rotationMatrix);
// newVMat.mul(translationMatrix);

// return newVMat;
// }

// public void moveBackward(float speed) {
// this.cameraX -= cameraN.get(0) * speed; // Move along -N
// this.cameraY -= cameraN.get(1) * speed;
// this.cameraZ -= cameraN.get(2) * speed;
// System.out.println("Object Reference: " + this);

// }

// public void moveLeft(float speed) {
// this.cameraX -= cameraU.get(0) * speed; // Move along -N
// this.cameraY -= cameraU.get(1) * speed;
// this.cameraZ -= cameraU.get(2) * speed;
// System.out.println("Object Reference: " + this);

// }

// public void moveRight(float speed) {
// this.cameraX += cameraU.get(0) * speed; // Move along -N
// this.cameraY += cameraU.get(1) * speed;
// this.cameraZ += cameraU.get(2) * speed;
// System.out.println("Object Reference: " + this);

// }

// public void moveForward(float speed) {
// this.cameraX += cameraN.get(0) * speed; // Move along +N
// this.cameraY += cameraN.get(1) * speed;
// this.cameraZ += cameraN.get(2) * speed;
// }

// public void moveUp(float speed) {
// cameraX += cameraV.get(0) * speed;
// cameraY += cameraV.get(1) * speed;
// cameraZ += cameraV.get(2) * speed;
// }

// public void moveDown(float speed) {
// cameraX -= cameraV.get(0) * speed;
// cameraY -= cameraV.get(1) * speed;
// cameraZ -= cameraV.get(2) * speed;
// }
//
// public void pan(float angle) {
// // 1. Rotate cameraN and cameraU around cameraV
// cameraU.rotateAxis(angle, cameraV.x, cameraV.y, cameraV.z);
// cameraN.rotateAxis(angle, cameraV.x, cameraV.y, cameraV.z);

// }

// // Pitch: Rotate cameraN and cameraV around the right vector (cameraU)
// public void pitch(float angle) {
// // Create rotation matrix around the RIGHT axis (cameraU)
// Matrix4f rotationMatrix = new Matrix4f().rotate(angle, this.cameraU);

// // Apply rotation to cameraN (forward) and cameraV (up)
// this.cameraN = rotationMatrix.transformDirection(this.cameraN);
// this.cameraV = rotationMatrix.transformDirection(this.cameraV);
// }

// public void setWorldAxes(float value) {
// this.lineBit = value;
// }

// public Vector3f getCameraN() {
// return this.cameraN;
// }

// }
