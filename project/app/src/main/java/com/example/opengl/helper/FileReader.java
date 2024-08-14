package com.example.opengl.helper;

import android.content.Context;

import com.example.opengl.Object.Material;
import com.example.opengl.Object.Vertex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileReader {
    // Constants for vertex attributes sizes
    private static final int VERTEX_COMPONENTS = 3;
    private static final int TEXTURE_COMPONENTS = 2;
    private static final int NORMAL_COMPONENTS = 3;
    private static final int COLOR_COMPONENTS = 4;
    private static final float DEFAULT_COLOR_VALUE = 1.0f;

    public static Vertex readObjFile(Context context, String fileName) {
        // Initialize lists to store vertex, texture, normal, and face data
        List<Float> vertexList = new ArrayList<>();
        List<Float> textureList = new ArrayList<>();
        List<Float> normalList = new ArrayList<>();
        List<String[]> faceList = new ArrayList<>();

        try {
            // Open the .obj file and create a buffered reader
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            // Read each line of the file
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String[] token = line.split("\\s+");
                    for (int i = 1; i < token.length; i++) {
                        vertexList.add(Float.parseFloat(token[i]));
                    }
                }
                if (line.startsWith("f ")) {
                    String[] token = line.split("\\s+");
                    String[] face = Arrays.copyOfRange(token, 1, token.length);
                    faceList.add(face);
                }
                if (line.startsWith("vt ")) {
                    String[] token = line.split("\\s+");
                    textureList.add(Float.parseFloat(token[1]) % 1);
                    textureList.add(1.0f - Float.parseFloat(token[2]) % 1);
                }
                if (line.startsWith("vn ")) {
                    String[] token = line.split("\\s+");
                    for (int i = 1; i < token.length; i++) {
                        normalList.add(Float.parseFloat(token[i]));
                    }
                }
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize lists to store vertex, normal, and texture data arranged according to face order
        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();

        // Iterate through the face list to determine corresponding vertex, normal, and texture data and store them in arrays
        for (String[] face : faceList) {
            int[][] quads;
            switch (face.length) {
                case 3:
                    quads = new int[][]{{0, 1, 2}};
                    break;
                case 4:
                    quads = new int[][]{{0, 1, 2}, {0, 2, 3}};
                    break;
                case 5:
                    quads = new int[][]{{0, 1, 2}, {0, 2, 3}, {0, 3, 4}};
                    break;
                default:
                    continue;
            }

            for (int[] quad : quads) {
                for (int index : quad) {
                    String token = face[index];
                    String[] tokens = token.split("/");
                    int vertexIndex = (Integer.parseInt(tokens[0]) - 1) * VERTEX_COMPONENTS;
                    int textureIndex = !tokens[1].isEmpty() ? (Integer.parseInt(tokens[1]) - 1) * TEXTURE_COMPONENTS : -1;
                    int normalIndex = (Integer.parseInt(tokens[2]) - 1) * NORMAL_COMPONENTS;

                    // Store vertex data
                    positions.add(vertexList.get(vertexIndex));
                    positions.add(vertexList.get(vertexIndex + 1));
                    positions.add(vertexList.get(vertexIndex + 2));

                    // Store normal data
                    normals.add(normalList.get(normalIndex));
                    normals.add(normalList.get(normalIndex + 1));
                    normals.add(normalList.get(normalIndex + 2));

                    // Store texture data
                    if (textureIndex != -1) {
                        textureCoords.add(textureList.get(textureIndex));
                        textureCoords.add(textureList.get(textureIndex + 1));
                    } else {
                        textureCoords.add(0.0f);
                        textureCoords.add(0.0f);
                    }
                }
            }
        }

        // Initialize float arrays to store vertex, normal, texture, and color data for each triangle
        float[] positionsArray = new float[positions.size()];
        float[] normalsArray = new float[normals.size()];
        float[] textureCoordsArray = new float[textureCoords.size()];
        float[] colorsArray = new float[positions.size() / VERTEX_COMPONENTS * COLOR_COMPONENTS];

        // Convert ArrayLists to float arrays
        for (int i = 0; i < positions.size(); i++) {
            positionsArray[i] = positions.get(i);
        }

        for (int i = 0; i < normals.size(); i++) {
            normalsArray[i] = normals.get(i);
        }

        for (int i = 0; i < textureCoords.size(); i++) {
            textureCoordsArray[i] = textureCoords.get(i);
        }

        // Set default color for each triangle
        Arrays.fill(colorsArray, DEFAULT_COLOR_VALUE);

        Vertex vertex = new Vertex(positionsArray, normalsArray, textureCoordsArray, colorsArray);
        return vertex;
    }

    public static Material readMtlFile(Context context, String fileName) {
        Material material = new Material();
        if (fileName.isEmpty()) {
            Arrays.fill(material.Ka, 0.5f);
            Arrays.fill(material.Kd, 1.0f);
            Arrays.fill(material.Ks, 1.0f);
            material.Tr = 0.0f;
            material.Ns = 35.0f;
            material.illum = 3.0f;
            return material;
        }
        // Read capsule.mtl
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Ka ")) {
                    String[] token = line.split("\\s+");
                    for (int i = 1; i < token.length; i++) {
                        material.Ka[i - 1] = Float.parseFloat(token[i]);
                    }
                }
                if (line.startsWith("Kd ")) {
                    String[] token = line.split("\\s+");
                    for (int i = 1; i < token.length; i++) {
                        material.Kd[i - 1] = Float.parseFloat(token[i]);
                    }
                }
                if (line.startsWith("Ks ")) {
                    String[] token = line.split("\\s+");
                    for (int i = 1; i < token.length; i++) {
                        material.Ks[i - 1] = Float.parseFloat(token[i]);
                    }
                }
                if (line.startsWith("Ns ")) {
                    String[] token = line.split("\\s+");
                    material.Ns = Float.parseFloat(token[1]);
                }
                if (line.startsWith("illum ")) {
                    String[] token = line.split("\\s+");
                    material.illum = Float.parseFloat(token[1]);
                }
                if (line.startsWith("Tr ")) {
                    String[] token = line.split("\\s+");
                    material.Tr = Float.parseFloat(token[1]);
                }
            }
            inputStream.close();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return material;
    }
}