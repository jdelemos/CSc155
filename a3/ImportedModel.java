package a3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import org.joml.*;
import org.joml.Vector3f;
import org.joml.Vector2f;

public class ImportedModel {
	private org.joml.Vector3f[] vertices;
	private org.joml.Vector3f[] normals;
	private org.joml.Vector2f[] texCoords;

	private int numVertices;

	public ImportedModel(String filename) {
		ModelImporter modelImporter = new ModelImporter();
		try {
			modelImporter.parseOBJ(filename);
			numVertices = modelImporter.getNumVertices();

			if (numVertices == 0) {
				System.err.println("Error: OBJ file contains no vertices!");
				return;
			}

			float[] verts = modelImporter.getVertices();
			float[] tcs = modelImporter.getTextureCoordinates();
			float[] norm = modelImporter.getNormals();

			if (verts == null || verts.length == 0 ||
					tcs == null || tcs.length == 0 ||
					norm == null || norm.length == 0) {
				System.err.println("Error: OBJ file is missing vertex, texture, or normal data!");
				return;
			}

			vertices = new Vector3f[numVertices];
			texCoords = new Vector2f[numVertices];
			normals = new Vector3f[numVertices];

			for (int i = 0; i < numVertices; i++) {
				vertices[i] = new Vector3f(verts[i * 3], verts[i * 3 + 1], verts[i * 3 + 2]);
				texCoords[i] = new Vector2f(tcs[i * 2], tcs[i * 2 + 1]);
				normals[i] = new Vector3f(norm[i * 3], norm[i * 3 + 1], norm[i * 3 + 2]);
			}
		} catch (IOException e) {
			System.err.println("Failed to load OBJ file: " + filename);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unexpected error in ImportedModel: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public int getNumVertices() {
		return numVertices;
	}

	public Vector3f[] getVertices() {
		return vertices;
	}

	public Vector2f[] getTexCoords() {
		return texCoords;
	}

	public Vector3f[] getNormals() {
		return normals;
	}

	private class ModelImporter { // values as read from OBJ file
		private ArrayList<Float> vertVals = new ArrayList<Float>();
		private ArrayList<Float> triangleVerts = new ArrayList<Float>();
		private ArrayList<Float> textureCoords = new ArrayList<Float>();

		// values stored for later use as vertex attributes
		private ArrayList<Float> stVals = new ArrayList<Float>();
		private ArrayList<Float> normals = new ArrayList<Float>();
		private ArrayList<Float> normVals = new ArrayList<Float>();

		public void parseOBJ(String filename) throws IOException {
			InputStream input = new FileInputStream(new File(filename));
			BufferedReader br = new BufferedReader(new InputStreamReader(input));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("v ")) { // vertex positions
					String[] tokens = line.substring(2).trim().split("\\s+");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							vertVals.add(Float.valueOf(token));
						}
					}
				} else if (line.startsWith("vt")) { // texture coordinates
					String[] tokens = line.substring(3).trim().split("\\s+");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							stVals.add(Float.valueOf(token));
						}
					}
				} else if (line.startsWith("vn")) { // vertex normals
					String[] tokens = line.substring(3).trim().split("\\s+");
					for (String token : tokens) {
						if (!token.isEmpty()) {
							normVals.add(Float.valueOf(token));
						}
					}
				} else if (line.startsWith("f")) { // faces
					String[] tokens = line.substring(2).trim().split("\\s+");
					for (String token : tokens) {
						if (token.isEmpty())
							continue;
						String[] parts = token.split("/");
						if (parts.length < 3)
							continue; // Skip if face data is incomplete
						String v = parts[0];
						String vt = parts[1];
						String vn = parts[2];

						int vertRef = (Integer.valueOf(v) - 1) * 3;
						int tcRef = (Integer.valueOf(vt) - 1) * 2;
						int normRef = (Integer.valueOf(vn) - 1) * 3;

						triangleVerts.add(vertVals.get(vertRef));
						triangleVerts.add(vertVals.get(vertRef + 1));
						triangleVerts.add(vertVals.get(vertRef + 2));

						textureCoords.add(stVals.get(tcRef));
						textureCoords.add(stVals.get(tcRef + 1));

						normals.add(normVals.get(normRef));
						normals.add(normVals.get(normRef + 1));
						normals.add(normVals.get(normRef + 2));
					}
				}
			}
			br.close();
			input.close();
		}

		public int getNumVertices() {
			return (triangleVerts.size() / 3);
		}

		public float[] getVertices() {
			float[] p = new float[triangleVerts.size()];
			for (int i = 0; i < triangleVerts.size(); i++) {
				p[i] = triangleVerts.get(i);
			}
			return p;
		}

		public float[] getTextureCoordinates() {
			float[] t = new float[(textureCoords.size())];
			for (int i = 0; i < textureCoords.size(); i++) {
				t[i] = textureCoords.get(i);
			}
			return t;
		}

		public float[] getNormals() {
			float[] n = new float[(normals.size())];
			for (int i = 0; i < normals.size(); i++) {
				n[i] = normals.get(i);
			}
			return n;
		}
	}
}
