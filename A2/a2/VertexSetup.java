package a2;

import com.jogamp.opengl.GL4;
import com.jogamp.common.nio.Buffers;
import java.nio.FloatBuffer;

public class VertexSetup {
    private int[] vbo;
    private int[] vao;

    public VertexSetup(int[] vao, int[] vbo) {
        this.vao = vao;
        this.vbo = vbo;
    }

    public void setupVertices(GL4 gl) {
        float[] pyramidPositions = {
                -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // front
                1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // right
                1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // back
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f, // left
                -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, // LF
                1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f // RR
        };

        float[] pyrTextureCoordinates = {
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
                0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
        };

        float[] ellipsePositions = {
                -18.0f, -6.0f, -1.1f,
                18.0f, -5.0f, -1.1f,
                18.0f, 1.5f, -1.1f,
                -15.0f, -.70f, -1.1f,
                -18.0f, -6.0f, -1.1f
        };

        float[] ellipseTextureCoordinates = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 1.0f
        };

        float[] skyPositions = {
                -20.0f, 8.0f, -1.5f,
                20.0f, 8.0f, -1.5f,
                20.0f, -5.0f, -1.5f,
                -20.0f, -5.0f, -1.5f,
                -20.0f, 8.0f, -1.5f
        };

        float[] circleVertices = generateCircle(0.0f, 0.0f, -1.0f, .70f, 70);
        float[] generateCircleTextureCoordinates = generateCircleTextureCoordinates(70);

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pyramidPositions);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer vertBuf1 = Buffers.newDirectFloatBuffer(pyramidPositions);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, vertBuf1.limit() * 4, vertBuf1, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(pyrTextureCoordinates);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[3]);
        FloatBuffer ellipseBuf = Buffers.newDirectFloatBuffer(ellipsePositions);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, ellipseBuf.limit() * 4, ellipseBuf, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[4]);
        FloatBuffer ellipseTexBuf = Buffers.newDirectFloatBuffer(ellipseTextureCoordinates);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, ellipseTexBuf.limit() * 4, ellipseTexBuf, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[5]);
        FloatBuffer skyBuf = Buffers.newDirectFloatBuffer(skyPositions);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, skyBuf.limit() * 4, skyBuf, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[6]);
        FloatBuffer circBuf = Buffers.newDirectFloatBuffer(circleVertices);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, circBuf.limit() * 4, circBuf, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, vbo[7]);
        FloatBuffer circBufTex = Buffers.newDirectFloatBuffer(generateCircleTextureCoordinates);
        gl.glBufferData(GL4.GL_ARRAY_BUFFER, circBufTex.limit() * 4, circBufTex, GL4.GL_STATIC_DRAW);

        gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, 0);
    }

    private float[] generateCircle(float cx, float cy, float cz, float radius, int segments) {
        float[] circleVertices = new float[segments * 3];
        float angleIncrement = (float) (2 * Math.PI / segments);

        for (int i = 0; i < segments; i++) {
            float angle = i * angleIncrement;
            float x = cx + radius * (float) Math.cos(angle);
            float y = cy + radius * (float) Math.sin(angle);
            float z = cz;

            circleVertices[i * 3] = x;
            circleVertices[i * 3 + 1] = y;
            circleVertices[i * 3 + 2] = z;
        }

        return circleVertices;
    }

    public float[] generateCircleTextureCoordinates(int segments) {
        float[] textureCoords = new float[segments * 2]; // Each vertex has 2 texture coordinates (u, v)
        float angleIncrement = (float) (2 * Math.PI / segments); // Angle between each segment

        for (int i = 0; i < segments; i++) {
            float angle = i * angleIncrement;

            // Calculate texture coordinates
            float u = 0.5f + 0.5f * (float) Math.cos(angle); // u ranges from 0 to 1
            float v = 0.5f + 0.5f * (float) Math.sin(angle); // v ranges from 0 to 1

            // Store the texture coordinates
            textureCoords[i * 2] = u;
            textureCoords[i * 2 + 1] = v;
        }

        return textureCoords;
    }

}