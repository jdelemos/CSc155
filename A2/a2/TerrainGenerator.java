package a2;

import java.util.Random;

public class TerrainGenerator {

    private static final int GRID_SIZE = 20; // Increase this for more detail
    private static final float TILE_SIZE = 2.0f; // Controls spacing of points
    private static final float HEIGHT_SCALE = 2.5f; // Controls terrain roughness

    public static void main(String[] args) {
        float[] groundPositions = generateGroundPositions(GRID_SIZE);
        float[] groundTextureCoordinates = generateGroundTextureCoordinates(GRID_SIZE);
        int[] groundIndices = generateGroundIndices(GRID_SIZE);

        // Print sample output (can be used for OpenGL rendering)
        System.out.println("Ground Positions: " + groundPositions.length / 3 + " vertices");
        System.out.println("Ground Texture Coordinates: " + groundTextureCoordinates.length / 2 + " UVs");
        System.out.println("Ground Indices: " + groundIndices.length + " indices (triangles)");
    }

    // Generate vertex positions for a sloped terrain using a noise function
    public static float[] generateGroundPositions(int gridSize) {
        float[] positions = new float[(gridSize + 1) * (gridSize + 1) * 3];
        Random random = new Random();

        for (int row = 0; row <= gridSize; row++) {
            for (int col = 0; col <= gridSize; col++) {
                int index = (row * (gridSize + 1) + col) * 3;

                float x = (col - gridSize / 2) * TILE_SIZE;
                float z = (row - gridSize / 2) * TILE_SIZE;
                float y = getPerlinNoise(x, z) * HEIGHT_SCALE; // Height variation

                positions[index] = x;
                positions[index + 1] = y;
                positions[index + 2] = z;
            }
        }
        return positions;
    }

    // Generate texture coordinates to map the texture properly
    public static float[] generateGroundTextureCoordinates(int gridSize) {
        float[] texCoords = new float[(gridSize + 1) * (gridSize + 1) * 2];

        for (int row = 0; row <= gridSize; row++) {
            for (int col = 0; col <= gridSize; col++) {
                int index = (row * (gridSize + 1) + col) * 2;

                texCoords[index] = (float) col / gridSize * 5.0f; // Scale for tiling
                texCoords[index + 1] = (float) row / gridSize * 5.0f;
            }
        }
        return texCoords;
    }

    // Generate indices for rendering the ground as a triangle mesh
    public static int[] generateGroundIndices(int gridSize) {
        int[] indices = new int[gridSize * gridSize * 6];
        int index = 0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int topLeft = row * (gridSize + 1) + col;
                int topRight = topLeft + 1;
                int bottomLeft = (row + 1) * (gridSize + 1) + col;
                int bottomRight = bottomLeft + 1;

                // First triangle
                indices[index++] = topLeft;
                indices[index++] = bottomLeft;
                indices[index++] = topRight;

                // Second triangle
                indices[index++] = topRight;
                indices[index++] = bottomLeft;
                indices[index++] = bottomRight;
            }
        }
        return indices;
    }

    // Simple Perlin noise function for terrain height variation
    public static float getPerlinNoise(float x, float z) {
        return (float) (Math.sin(x * 0.1) * Math.cos(z * 0.1)); // Simple wave-based height
    }
}
