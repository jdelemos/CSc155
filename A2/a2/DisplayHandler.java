package a2;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class DisplayHandler {
    private int renderingProgram;
    private int[] vbo;
    float cloudx = 0;
    private int mvLoc, pLoc;
    private Matrix4f pMat, vMat, mMat, mvMat;
    private FloatBuffer vals;
    public float cameraX;
    public float cameraY;
    public float cameraZ;
    private int pyramidTexture, ellipseTexture, skyTexture, moonTexture,
            cloudTexture, redTextureColor, greenTextureColor, blueTextureColor;
    private Vector3f cameraU = new Vector3f();
    private Vector3f cameraV = new Vector3f();
    private Vector3f cameraN = new Vector3f();
    private com.jogamp.opengl.math.Matrix4f tMatrix, rMatrix;
    private float speed = 0.1f;
    private float rotationSpeed = 0.05f;

    // constructor
    public DisplayHandler(int renderingProgram, int[] vbo, int mvLoc, int pLoc, Matrix4f pMat, Matrix4f vMat,
            Matrix4f mMat, Matrix4f mvMat, FloatBuffer vals, int pyramidTexture, int ellipseTexture, int skyTexture,
            int moonTexture, int cloudTexture, float cameraX, float cameraY, float cameraZ, Vector3f cameraU,
            Vector3f cameraV, Vector3f cameraN, int redTextureColor, int greenTextureColor, int blueTextureColor) {
        this.renderingProgram = renderingProgram;
        this.vbo = vbo;
        this.mvLoc = mvLoc;
        this.pLoc = pLoc;
        this.pMat = pMat;
        this.vMat = vMat;
        this.mMat = mMat;
        this.mvMat = mvMat;
        this.vals = vals;
        this.pyramidTexture = pyramidTexture;
        this.ellipseTexture = ellipseTexture;
        this.skyTexture = skyTexture;
        this.moonTexture = moonTexture;
        this.cloudTexture = cloudTexture;
        this.redTextureColor = redTextureColor;
        this.greenTextureColor = greenTextureColor;
        this.blueTextureColor = blueTextureColor;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
        this.cameraU = cameraU;
        this.cameraV = cameraV;
        this.cameraN = cameraN;

    }

    // display function
    public void display(GL4 gl, float cameraX, float cameraY, float cameraZ, float aspect, double circleAngle) {
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);

        pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
        // System.out.println("Camera Position: (" + cameraX + ", " + cameraY + ", " +
        // cameraZ + ")");

        vMat = buildViewMatrix(0, 0, -8.0f);
        // Print View Matrix (vMat) for Debugging SUPER USEFUL
        float[] debugVals = new float[16];
        vMat.get(debugVals);

        System.out.println("View Matrix (vMat):");
        for (int i = 0; i < 4; i++) {
            System.out.printf("| %6.2f %6.2f %6.2f %6.2f |\n",
                    debugVals[i], debugVals[i + 4], debugVals[i + 8], debugVals[i + 12]);
        }

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, pyramidTexture);
        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);

        // cloud movement
        cloudx += .01f;
        if (cloudx > 15.0f) {
            cloudx = -9.0f;
        }

        // moon movement
        float x = 5.0f * (float) Math.cos(circleAngle);
        float y = 5.0f * (float) Math.sin(circleAngle);

        drawObject(gl, vbo[0], vbo[2], -2.0f, -1.0f, 0.0f);
        drawObject(gl, vbo[0], vbo[2], 2.7f, -1.0f, -2.0f);
        drawObject(gl, vbo[0], vbo[2], .5f, -1.0f, -1.0f);
        drawCircle(gl, vbo[6], moonTexture, vbo[7], x, y, -0.10f);
        drawGround(gl, vbo[3], vbo[4], ellipseTexture);
        drawSky(gl, vbo[5], vbo[4], skyTexture);
        drawLine(gl, vbo[9], ellipseTexture, redTextureColor);
        drawLine(gl, vbo[10], ellipseTexture, greenTextureColor);
        drawLine(gl, vbo[11], ellipseTexture, blueTextureColor);

        // Could have done this dynamically but thought it wasn't a great idea to waste
        // time.
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + .5f, 3.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx, 3.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + .25f, 3.5f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + .25f, 3.5f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - .5f, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - .25f, 2.5f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - .25f, 2.5f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 3.5f, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 3.0f, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 3.25f, 2.5f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 3.25f, 2.5f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 3.5f, 3.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 3.0f, 3.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 3.25f, 3.0f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 3.25f, 3.0f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 1.5f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 1.0f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 1.25f, 2.0f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 1.25f, 2.0f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 4.5f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 4.0f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 4.25f, 2.0f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 4.25f, 2.0f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 4.5f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 4.0f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 4.25f, 2.0f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx + 4.25f, 2.0f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 5.5f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 5.0f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 5.25f, 2.0f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 5.25f, 2.0f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 6.5f, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 6.0f, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 6.25f, 2.5f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 6.25f, 2.5f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 7.5f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 7.0f, 2.0f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 7.25f, 2.0f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 7.25f, 2.0f - .1f, 0);

        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 8.5f, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 8.0f, 2.5f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 8.25f, 2.5f + .15f, 0);
        drawCircle(gl, vbo[8], cloudTexture, vbo[7], cloudx - 8.25f, 2.5f - .1f, 0);

        // since the matrices are being updated all the time, we need to bind them.
        mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix"); // View Matrix
        pLoc = gl.glGetUniformLocation(renderingProgram, "p_matrix"); // Projection Matrix

        // Upload View Matrix (mv_matrix)
        gl.glUniformMatrix4fv(mvLoc, 1, false, vMat.get(vals));
        // Upload Projection Matrix (p_matrix)
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

    }

    // draw object functions. These are are basically the same.
    private void drawObject(GL4 gl, int vboID, int textureCoord, float locX, float locY, float locZ) {
        mMat.identity();
        mMat.translate(locX, locY, locZ);

        mvMat.identity();
        mvMat.mul(vMat);
        mvMat.mul(mMat);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, pyramidTexture);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vboID);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, textureCoord);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 18);
    }

    private void drawEllipse(GL4 gl, int vboID, int tex) {
        mMat.identity();
        mMat.translate(0.0f, 0.0f, 0.0f);

        mvMat.identity();
        mvMat.mul(vMat);
        mvMat.mul(mMat);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, tex);
        gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "textureSampler"), 0);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vboID);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, 15);
    }

    private void drawCircle(GL4 gl, int vboID, int tex, int textureCoord, float x, float y, float z) {
        mMat.identity();
        mMat.translate(x, y, z);

        mvMat.identity();
        mvMat.mul(vMat);
        mvMat.mul(mMat);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, tex);
        gl.glUniform1i(gl.glGetUniformLocation(renderingProgram, "textureSampler"), 0);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vboID);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, textureCoord);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glDrawArrays(GL4.GL_TRIANGLE_FAN, 0, 70);
    }

    private void drawGround(GL4 gl, int groundVBO, int groundTexVBO, int groundTexture) {
        mMat.identity();
        mMat.translate(0.0f, 0.0f, 0.0f); // Place it at the default position

        mvMat.identity();
        mvMat.mul(vMat); // Apply camera transformation
        mvMat.mul(mMat); // Apply model transformation

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, groundTexture);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundVBO);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundTexVBO);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 6);
    }

    private void drawLine(GL4 gl, int LineVBO, int LineTexVBO, int groundTexture) {
        mMat.identity();
        mMat.translate(0.0f, 0.0f, 0.0f); // Place it at the default position

        mvMat.identity();
        mvMat.mul(vMat); // Apply camera transformation
        mvMat.mul(mMat); // Apply model transformation

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, groundTexture);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, LineVBO);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, LineTexVBO);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glLineWidth(5.0f); // Set the desired line width s

        gl.glDrawArrays(GL4.GL_LINES, 0, 2);
    }

    private void drawSky(GL4 gl, int groundVBO, int groundTexVBO, int groundTexture) {
        mMat.identity();
        mMat.translate(0.0f, 0.0f, 0.0f); // Place it at the default position

        mvMat.identity();
        mvMat.mul(vMat); // Apply camera transformation
        mvMat.mul(mMat); // Apply model transformation

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, groundTexture);

        gl.glUniformMatrix4fv(mvLoc, 1, false, mvMat.get(vals));
        gl.glUniformMatrix4fv(pLoc, 1, false, pMat.get(vals));

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundVBO);
        gl.glVertexAttribPointer(0, 3, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, groundTexVBO);
        gl.glVertexAttribPointer(1, 2, GL4.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glDrawArrays(GL4.GL_TRIANGLES, 0, 6);
    }

    // ---------------------Camera
    // functions-----------------------------------------//

    public Matrix4f buildViewMatrix(float ux, float uy, float uz) {
        // Construct the new rotation matrix (R)
        Matrix4f rotationMatrix = new Matrix4f();
        rotationMatrix.set(
                cameraU.get(0), cameraU.get(1), cameraU.get(2), 0,
                cameraV.get(0), cameraV.get(1), cameraV.get(2), 0,
                -cameraN.get(0), -cameraN.get(1), -cameraN.get(2), 0,
                0, 0, 0, 1);

        // Construct the new translation matrix (T)
        Matrix4f translationMatrix = new Matrix4f();
        translationMatrix.identity();
        translationMatrix.set(3, 0, -cameraX); // -Cx
        translationMatrix.set(3, 1, -cameraY); // -Cy
        translationMatrix.set(3, 2, -cameraZ); // -Cz

        // System.out.println("buildViewMatrix: cameraX=" + cameraX + ", cameraY=" +
        // cameraY + ", cameraZ=" + cameraZ);

        // Calculation
        Matrix4f newVMat = new Matrix4f();
        newVMat.identity();
        newVMat.mul(rotationMatrix);
        newVMat.mul(translationMatrix);

        return newVMat;
    }

    public void moveForward(float speed) {
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

    public void moveBackward(float speed) {
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

}
