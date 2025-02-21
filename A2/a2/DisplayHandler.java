package a2;

import java.nio.FloatBuffer;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import org.joml.Matrix4f;

public class DisplayHandler {
    private int renderingProgram;
    private int[] vbo;
    private int mvLoc, pLoc;
    private Matrix4f pMat, vMat, mMat, mvMat;
    private FloatBuffer vals;
    private int pyramidTexture, ellipseTexture, skyTexture, moonTexture;

    public DisplayHandler(int renderingProgram, int[] vbo, int mvLoc, int pLoc, Matrix4f pMat, Matrix4f vMat,
            Matrix4f mMat, Matrix4f mvMat, FloatBuffer vals, int pyramidTexture, int ellipseTexture, int skyTexture,
            int moonTexture) {
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
    }

    public void display(GL4 gl, float cameraX, float cameraY, float cameraZ, float aspect, double circleAngle) {
        gl.glClear(GL4.GL_COLOR_BUFFER_BIT | GL4.GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);

        pMat.setPerspective((float) Math.toRadians(60.0f), aspect, 0.1f, 1000.0f);
        vMat.translation(-cameraX, -cameraY, -cameraZ);

        gl.glActiveTexture(GL4.GL_TEXTURE0);
        gl.glBindTexture(GL4.GL_TEXTURE_2D, pyramidTexture);
        gl.glEnable(GL4.GL_DEPTH_TEST);
        gl.glDepthFunc(GL4.GL_LEQUAL);

        float x = 5.0f * (float) Math.cos(circleAngle);
        float y = 5.0f * (float) Math.sin(circleAngle);

        drawObject(gl, vbo[0], vbo[2], -2.0f, -1.0f, 0.0f);
        drawObject(gl, vbo[0], vbo[2], 2.7f, -.25f, 0.0f);
        drawObject(gl, vbo[0], vbo[2], .5f, -.50f, 0.0f);
        drawCircle(gl, vbo[6], moonTexture, vbo[7], x, y, -0.10f);
        drawEllipse(gl, vbo[3], ellipseTexture);
        drawEllipse(gl, vbo[5], skyTexture);
    }

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
}