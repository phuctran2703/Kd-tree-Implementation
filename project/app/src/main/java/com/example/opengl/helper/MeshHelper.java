package com.example.opengl.helper;

import android.util.Log;

import com.example.opengl.Object.Ray;

import java.util.ArrayList;
import java.util.List;

public class MeshHelper {
    protected Ray ray;

    public MeshHelper(float[] rayPosition, float[] rayDirection) {
        ray = new Ray(rayPosition, rayDirection);
    }

    protected float dotProduct(float[] v1, float[] v2) {
        return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
    }

    protected float[] crossProduct(float[] a, float[] b) {
        float[] result = new float[3];
        result[0] = a[1] * b[2] - a[2] * b[1];
        result[1] = a[2] * b[0] - a[0] * b[2];
        result[2] = a[0] * b[1] - a[1] * b[0];
        return result;
    }

    protected float s(float[] a, float[] b) {
        float[] cross = crossProduct(a, b);
        float magnitude = (float) Math.sqrt(dotProduct(cross, cross));
        return magnitude / 2.0f;
    }

    protected float det2(float a00, float a01, float a10, float a11){
        return a00*a11 - a01*a10;
    }
}

