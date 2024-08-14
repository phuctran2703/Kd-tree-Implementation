package com.example.opengl.test_spatial_structure;

import com.example.opengl.Object.AABB;
import com.example.opengl.Object.CarComponent;
import com.example.opengl.Object.KDNode;
import com.example.opengl.helper.KDNodeHelper;
import com.example.opengl.helper.TestHelper;
import com.example.opengl.helper.carComponentHelper;

import java.util.Arrays;
import java.util.Map;

public class Unit {
    public static class UnitInput{
        public int n_rays;
        public int n_aabbs;
        public float[] origins;
        public float[] directions;
        public float[] min;
        public float[] max;

        public UnitInput(int n_rays, int n_aabbs, float[] origins, float[] directions, float[] min, float[] max){
            this.n_rays = n_rays;
            this.n_aabbs = n_aabbs;
            this.origins = origins;
            this.directions = directions;
            this.min = min;
            this.max = max;
        }
    }
    public static class UnitOutput{
        Map<Integer, int[]> outputs;

        public UnitOutput(Map<Integer, int[]> outputs){
            this.outputs = outputs;
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UnitOutput{\n");
            for (Map.Entry<Integer, int[]> entry : outputs.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ");
                sb.append(Arrays.toString(entry.getValue())).append("\n");
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public static UnitOutput functionTest(UnitInput input){
        return TestHelper.functionTest(input);
    }
}
