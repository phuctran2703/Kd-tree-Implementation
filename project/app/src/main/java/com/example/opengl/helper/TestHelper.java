package com.example.opengl.helper;

import com.example.opengl.Object.AABB;
import com.example.opengl.Object.CarComponent;
import com.example.opengl.Object.KDNode;
import com.example.opengl.Object.Ray;
import com.example.opengl.test_spatial_structure.Unit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestHelper{
    KDNode rootNode;

    public TestHelper(KDNode rootNode) {
        this.rootNode = rootNode;
    }

    public int[] getIntersectionWithRayTest(float[] rayPosition, float[] rayDirection) {
        Set<Integer> intersections = new HashSet<>();

        float[] intersectionPoints = new AABBHelper(rayPosition, rayDirection,
                rootNode.coverBoundingBox.pMin,
                rootNode.coverBoundingBox.pMax).getIntersectionPoints();
        if (intersectionPoints == null) {
            return intersections.stream().mapToInt(Integer::intValue).toArray();
        }

        if (rootNode.carComponents != null) {
            for (CarComponent carComponent : rootNode.carComponents) {
                float[] intersectionPoint = new AABBHelper(rayPosition, rayDirection,
                        carComponent.aabb.pMin,
                        carComponent.aabb.pMax).getIntersectionPoints();
                if (intersectionPoint != null) {
                    intersections.add(carComponent.index);
                }
            }
        }

        if (rootNode.leftNode != null) {
            int[] leftIntersections = new TestHelper(rootNode.leftNode).getIntersectionWithRayTest(rayPosition, rayDirection);
            for (int index : leftIntersections) {
                intersections.add(index);
            }
        }

        if (rootNode.rightNode != null) {
            int[] rightIntersections = new TestHelper(rootNode.rightNode).getIntersectionWithRayTest(rayPosition, rayDirection);
            for (int index : rightIntersections) {
                intersections.add(index);
            }
        }

        return intersections.stream().mapToInt(Integer::intValue).toArray();
    }

    public Map<Integer, int[]> getIntersectWithMultiRayTest(Ray[] ray){
        Map<Integer, int[]> intersections = new HashMap<>();
        for (int i = 0; i < ray.length; i++) {
            int[] componentIndexs = this.getIntersectionWithRayTest(ray[i].rayPosition, ray[i].rayDirection);
            Arrays.sort(componentIndexs);
            intersections.put(i, componentIndexs);
        }
        return intersections;
    }

    public static Unit.UnitOutput functionTest(Unit.UnitInput input){
        AABB[] aabbs = new AABB[input.n_aabbs];
        for (int i = 0; i < input.n_aabbs; i++) {
            aabbs[i] = new AABB(new float[]{input.min[i*3], input.min[i*3+1], input.min[i*3+2]}, new float[]{input.max[i*3], input.max[i*3+1], input.max[i*3+2]});
        }
        CarComponent[] components = new CarComponent[aabbs.length];
        for (int i = 0; i < aabbs.length; i++) {
            components[i] = new CarComponent(aabbs[i], i);
        }
        AABB coverAABB = TestHelper.getBoundingBox(input.min, input.max);
        KDNode rootNode = KDNode.buildKdTree(coverAABB, components, 0);

        Ray[] rays = new Ray[input.n_rays];
        for (int i = 0; i < input.n_rays; i++) {
            rays[i] = new Ray(new float[]{input.origins[i*3], input.origins[i*3+1], input.origins[i*3+2]}, new float[]{input.directions[i*3], input.directions[i*3+1], input.directions[i*3+2]});
        }

        Map<Integer, int[]> intersectIndex  = new TestHelper(rootNode).getIntersectWithMultiRayTest(rays);
        Unit.UnitOutput ouput = new Unit.UnitOutput(intersectIndex);
        return ouput;
    }

    public static AABB getBoundingBox(float[]  min, float[] max){
        float[] pMin = new float[]{min[0], min[1], min[2]};
        float[] pMax = new float[]{max[0], max[1], max[2]};

        for (int i=3; i<min.length; i+=3){
            if (pMin[0] > min[i]) pMin[0] = min[i];
            if (pMin[1] > min[i+1]) pMin[1] = min[i+1];
            if (pMin[2] > min[i+2]) pMin[2] = min[i+2];
        }

        for (int i=3; i<max.length; i+=3){
            if (pMax[0] < max[i]) pMax[0] = max[i];
            if (pMax[1] < max[i+1]) pMax[1] = max[i+1];
            if (pMax[2] < max[i+2]) pMax[2] = max[i+2];
        }

        return new AABB(pMin, pMax);
    }
}
