package com.example.opengl.helper;

import android.util.Log;

import com.example.opengl.Object.AABB;
import com.example.opengl.Object.CarComponent;
import com.example.opengl.Object.KDNode;
import com.example.opengl.Object.Ray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KDNodeHelper{
    KDNode rootNode;

    public KDNodeHelper(KDNode rootNode) {
        this.rootNode = rootNode;
    }

    public CarComponent[] getIntersectionWithRay(float[] rayPosition, float[] rayDirection) {
        List<CarComponent> intersections = new ArrayList<>();

        float[] intersectionPoints = new AABBHelper(rayPosition, rayDirection, rootNode.coverBoundingBox.pMin, rootNode.coverBoundingBox.pMax).getIntersectionPoints();
        if (intersectionPoints == null) {
            return intersections.toArray(new CarComponent[0]);
        }
        if (rootNode.carComponents != null) {
            for (CarComponent carComponent : rootNode.carComponents) {
                if (carComponent.flag == true) {
                    continue;
                }
                float[] intersectionPoint = new AABBHelper(rayPosition, rayDirection, carComponent.aabb.pMin, carComponent.aabb.pMax).getIntersectionPoints();
                if (intersectionPoint != null) {
                    carComponent.flag = true;
                    intersections.add(carComponent);
                }
            }
            return intersections.toArray(new CarComponent[0]);
        }
        if (rootNode.leftNode != null) {
            intersections.addAll(Arrays.asList(new KDNodeHelper(rootNode.leftNode).getIntersectionWithRay(rayPosition, rayDirection)));
        }
        if (rootNode.rightNode != null) {
            intersections.addAll(Arrays.asList(new KDNodeHelper(rootNode.rightNode).getIntersectionWithRay(rayPosition, rayDirection)));
        }
        return intersections.toArray(new CarComponent[0]);
    }

    public CarComponent[] getIntersectionWithMultipleRay(Ray[] ray){
        List<CarComponent> intersections = new ArrayList<>();
        for (int i = 0; i < ray.length; i++) {
            CarComponent[] carComponents = this.getIntersectionWithRay(ray[i].rayPosition, ray[i].rayDirection);
            intersections.addAll(Arrays.asList(carComponents));
        }
        return intersections.toArray(new CarComponent[0]);
    }

}
