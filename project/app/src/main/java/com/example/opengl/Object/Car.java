package com.example.opengl.Object;

import android.content.Context;

import com.example.opengl.helper.FileReader;

public class Car{
    public Vertex vertex;
    public Material material;
    public Car(Vertex vertex, Material material) {
        this.vertex = vertex;
        this.material = material;
    }

    public Car(Context context, String fileName) {
        this.vertex = FileReader.readObjFile(context, fileName);
    }

    public float[] generate() {
        float[] vertices = new float[this.vertex.positions.length+this.vertex.textureCoords.length];
        int vertexIndex = 0;
        for (int i = 0; i < this.vertex.positions.length / 3; i++) {
            // Add position coordinates (x, y, z)
            vertices[vertexIndex++] = this.vertex.positions[i * 3];
            vertices[vertexIndex++] = this.vertex.positions[i * 3 + 1];
            vertices[vertexIndex++] = this.vertex.positions[i * 3 + 2];
            // Add texture coordinates (u, v)
            vertices[vertexIndex++] = this.vertex.textureCoords[i * 2];
            vertices[vertexIndex++] = this.vertex.textureCoords[i * 2 + 1];
        }
        return vertices;
    }

    public AABB getBoundingBox(){
        float[] pMin = new float[]{this.vertex.positions[0],this.vertex.positions[1],this.vertex.positions[2]};
        float[] pMax = new float[]{this.vertex.positions[0],this.vertex.positions[1],this.vertex.positions[2]};
        for (int i = 3; i < this.vertex.positions.length; i += 3) {
            if (this.vertex.positions[i] < pMin[0]) pMin[0] = this.vertex.positions[i];
            if (this.vertex.positions[i] > pMax[0]) pMax[0] = this.vertex.positions[i];
            if (this.vertex.positions[i + 1] < pMin[1]) pMin[1] = this.vertex.positions[i + 1];
            if (this.vertex.positions[i + 1] > pMax[1]) pMax[1] = this.vertex.positions[i + 1];
            if (this.vertex.positions[i + 2] < pMin[2]) pMin[2] = this.vertex.positions[i + 2];
            if (this.vertex.positions[i + 2] > pMax[2]) pMax[2] = this.vertex.positions[i + 2];
        }
        return new AABB(pMin, pMax);
    }
}