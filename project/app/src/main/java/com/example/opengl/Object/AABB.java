package com.example.opengl.Object;

public class AABB {
    public float[] pMin;
    public float[] pMax;

    public AABB(float[] pMin, float[] pMax) {
        this.pMin = pMin;
        this.pMax = pMax;
    }

    public float[] generate() {
        float[] vertices = AABB.generateVertices(this.pMin, this.pMax);
        short[] indices = AABB.generateIndices();
        float[] result = new float[indices.length * 3];

        for (int i = 0; i < indices.length; i++) {
            int vertexIndex = indices[i] * 3;
            result[i * 3] = vertices[vertexIndex];
            result[i * 3 + 1] = vertices[vertexIndex + 1];
            result[i * 3 + 2] = vertices[vertexIndex + 2];
        }

        return result;
    }

    public static float[] generateVertices(float[] pMin, float[] pMax) {
        return new float[]{
                pMin[0], pMin[1], pMin[2],
                pMax[0], pMin[1], pMin[2],
                pMax[0], pMax[1], pMin[2],
                pMin[0], pMax[1], pMin[2],
                pMin[0], pMin[1], pMax[2],
                pMax[0], pMin[1], pMax[2],
                pMax[0], pMax[1], pMax[2],
                pMin[0], pMax[1], pMax[2]
        };
    }

    public static short[] generateIndices() {
        return new short[]{
                // edges connecting the front and back faces
                0, 1, 1, 2, 2, 3, 3, 0,
                // edges connecting the front face to the back face
                0, 4, 1, 5, 2, 6, 3, 7,
                // edges connecting the back faces
                4, 5, 5, 6, 6, 7, 7, 4
        };
    }

    public Boolean checkIntersectTwoAABB(AABB a){
        if (a.pMax[0] < this.pMin[0] || a.pMin[0] > this.pMax[0]) {
            return false;
        }
        if (a.pMax[1] < this.pMin[1] || a.pMin[1] > this.pMax[1]) {
            return false;
        }
        if (a.pMax[2] < this.pMin[2] || a.pMin[2] > this.pMax[2]) {
            return false;
        }
        return true;
    }

    public float[] findIntersectTwoAABBInterval(AABB a, float[] direction, float t) {
        float tMinX, tMinY, tMinZ, tMin, tMaxX, tMaxY, tMaxZ, tMax;
        // Check X direction
        if (direction[0] > 0) {
            if (a.pMin[0] > this.pMax[0] || a.pMax[0] + direction[0] * t < this.pMin[0]) return null;
            tMinX = (this.pMin[0] - a.pMax[0]) / direction[0];
            tMaxX = (this.pMax[0] - a.pMin[0]) / direction[0];
            if (tMinX < 0) tMinX = 0;
            if (tMaxX > t) tMaxX = t;
        } else if (direction[0] < 0) {
            if (a.pMax[0] < this.pMin[0] || a.pMin[0] + direction[0] * t > this.pMax[0]) return null;
            tMinX = (this.pMax[0] - a.pMin[0]) / direction[0];
            tMaxX = (this.pMin[0] - a.pMax[0]) / direction[0];
            if (tMinX < 0) tMinX = 0;
            if (tMaxX > t) tMaxX = t;
        } else {
            if (a.pMin[0] > this.pMax[0] || a.pMax[0] < this.pMin[0]) return null;
            tMinX = 0;
            tMaxX = t;
        }

        // Check Y direction
        if (direction[1] > 0) {
            if (a.pMin[1] > this.pMax[1] || a.pMax[1] + direction[1] * t < this.pMin[1]) return null;
            tMinY = (this.pMin[1] - a.pMax[1]) / direction[1];
            tMaxY = (this.pMax[1] - a.pMin[1]) / direction[1];
            if (tMinY < 0) tMinY = 0;
            if (tMaxY > t) tMaxY = t;
        } else if (direction[1] < 0) {
            if (a.pMax[1] < this.pMin[1] || a.pMin[1] + direction[1] * t > this.pMax[1]) return null;
            tMinY = (this.pMax[1] - a.pMin[1]) / direction[1];
            tMaxY = (this.pMin[1] - a.pMax[1]) / direction[1];
            if (tMinY < 0) tMinY = 0;
            if (tMaxY > t) tMaxY = t;
        } else {
            if (a.pMin[1] > this.pMax[1] || a.pMax[1] < this.pMin[1]) return null;
            tMinY = 0;
            tMaxY = t;
        }

        // Check Z direction
        if (direction[2] > 0) {
            if (a.pMin[2] > this.pMax[2] || a.pMax[2] + direction[2] * t < this.pMin[2]) return null;
            tMinZ = (this.pMin[2] - a.pMax[2]) / direction[2];
            tMaxZ = (this.pMax[2] - a.pMin[2]) / direction[2];
            if (tMinZ < 0) tMinZ = 0;
            if (tMaxZ > t) tMaxZ = t;
        } else if (direction[2] < 0) {
            if (a.pMax[2] < this.pMin[2] || a.pMin[2] + direction[2] * t > this.pMax[2]) return null;
            tMinZ = (this.pMax[2] - a.pMin[2]) / direction[2];
            tMaxZ = (this.pMin[2] - a.pMax[2]) / direction[2];
            if (tMinZ < 0) tMinZ = 0;
            if (tMaxZ > t) tMaxZ = t;
        } else {
            if (a.pMin[2] > this.pMax[2] || a.pMax[2] < this.pMin[2]) return null;
            tMinZ = 0;
            tMaxZ = t;
        }

        // Calculate the intersection intervals
        tMin = Math.max(tMinX, Math.max(tMinY, tMinZ));
        tMax = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));

        if (tMin <= tMax) {
            return new float[] {tMin, tMax};
        } else {
            return null;
        }
    }
}


