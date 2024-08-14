package com.example.opengl.helper;

import com.example.opengl.Object.AABB;

public class AABBHelper extends MeshHelper {
    AABB aabb;
    private float[] intersectionPoint;

    public AABBHelper(float[] rayPosition, float[] rayDirection, float[] pMin, float[] pMax) {
        super(rayPosition, rayDirection);
        this.aabb = new AABB(pMin, pMax);
    }

    public float[] getIntersectionPoints() {
        float tMinX, tMinY, tMinz, tMin, tMaxX, tMaxY, tMaxz, tMax;
        if (ray.rayDirection[0] > 0) {
            tMinX = (aabb.pMin[0] - ray.rayPosition[0]) / ray.rayDirection[0];
            tMaxX = (aabb.pMax[0] - ray.rayPosition[0]) / ray.rayDirection[0];
        }
        else if (ray.rayDirection[0] < 0) {
            tMinX = (aabb.pMax[0] - ray.rayPosition[0]) / ray.rayDirection[0];
            tMaxX = (aabb.pMin[0] - ray.rayPosition[0]) / ray.rayDirection[0];
        }
        else {
            if (ray.rayPosition[0] < aabb.pMin[0] || ray.rayPosition[0] > aabb.pMax[0]) return null;
            tMinX = -Float.MAX_VALUE;
            tMaxX = Float.MAX_VALUE;
        }

        if (ray.rayDirection[1] > 0) {
            tMinY = (aabb.pMin[1] - ray.rayPosition[1]) / ray.rayDirection[1];
            tMaxY = (aabb.pMax[1] - ray.rayPosition[1]) / ray.rayDirection[1];
        }
        else if (ray.rayDirection[1] < 0) {
            tMinY = (aabb.pMax[1] - ray.rayPosition[1]) / ray.rayDirection[1];
            tMaxY = (aabb.pMin[1] - ray.rayPosition[1]) / ray.rayDirection[1];
        }
        else {
            if (ray.rayPosition[1] < aabb.pMin[1] || ray.rayPosition[1] > aabb.pMax[1]) return null;
            tMinY = -Float.MAX_VALUE;
            tMaxY = Float.MAX_VALUE;
        }

        if (ray.rayDirection[2] > 0) {
            tMinz = (aabb.pMin[2] - ray.rayPosition[2]) / ray.rayDirection[2];
            tMaxz = (aabb.pMax[2] - ray.rayPosition[2]) / ray.rayDirection[2];
        }
        else if (ray.rayDirection[2] < 0) {
            tMinz = (aabb.pMax[2] - ray.rayPosition[2]) / ray.rayDirection[2];
            tMaxz = (aabb.pMin[2] - ray.rayPosition[2]) / ray.rayDirection[2];
        }
        else {
            if (ray.rayPosition[2] < aabb.pMin[2] || ray.rayPosition[2] > aabb.pMax[2]) return null;
            tMinz = -Float.MAX_VALUE;
            tMaxz = Float.MAX_VALUE;
        }

        tMin = Math.max(tMinX, Math.max(tMinY, tMinz));
        tMax = Math.min(tMaxX, Math.min(tMaxY, tMaxz));

        if (tMin > tMax) return null;
        if (tMax < 0) return null;

        if (tMin<0 || tMin == tMax) {
            this.intersectionPoint = new float[]{
                    ray.rayPosition[0] + tMax * ray.rayDirection[0],
                    ray.rayPosition[1] + tMax * ray.rayDirection[1],
                    ray.rayPosition[2] + tMax * ray.rayDirection[2]
            };
            return intersectionPoint;
        }
        this.intersectionPoint = new float[]{
                ray.rayPosition[0] + tMin * ray.rayDirection[0],
                ray.rayPosition[1] + tMin * ray.rayDirection[1],
                ray.rayPosition[2] + tMin * ray.rayDirection[2],
                ray.rayPosition[0] + tMax * ray.rayDirection[0],
                ray.rayPosition[1] + tMax * ray.rayDirection[1],
                ray.rayPosition[2] + tMax * ray.rayDirection[2]
        };

        return this.intersectionPoint;
    }
}
