package com.example.opengl.Object;

public class CarComponent {
    public float[] triangle;
    public float[] textureCoords;
    public AABB aabb;
    public boolean flag = false;
    public int index;

    public CarComponent(float[] triangle, float[] textureCoords) {
        this.triangle = triangle;
        this.textureCoords = textureCoords;
        this.aabb = getBoundingBox(triangle);
    }

    public CarComponent(AABB aabb, int index) {
        this.triangle = null;
        this.textureCoords = null;
        this.aabb = aabb;
        this.index = index;
    }

    public static CarComponent[] getCarComponents(Car car) {
        int numComponents = car.vertex.positions.length / 9;
        CarComponent[] carComponentArray = new CarComponent[numComponents];
        int index = 0;

        for (int i = 0; i < car.vertex.positions.length; i += 9) {
            float[] newTriangle = new float[9];
            System.arraycopy(car.vertex.positions, i, newTriangle, 0, 9);

            int j = (i / 9) * 6;
            float[] newTextureCoords = new float[6];
            System.arraycopy(car.vertex.textureCoords, j, newTextureCoords, 0, 6);

            carComponentArray[index++] = new CarComponent(newTriangle, newTextureCoords);
        }

        return carComponentArray;
    }

    public static AABB getBoundingBox(float[] triangle) {
        float[] pMin = new float[]{triangle[0], triangle[1], triangle[2]};
        float[] pMax = new float[]{triangle[0], triangle[1], triangle[2]};

        for (int i = 3; i < triangle.length; i += 3) {
            if (triangle[i] < pMin[0]) pMin[0] = triangle[i];
            if (triangle[i] > pMax[0]) pMax[0] = triangle[i];

            if (triangle[i + 1] < pMin[1]) pMin[1] = triangle[i + 1];
            if (triangle[i + 1] > pMax[1]) pMax[1] = triangle[i + 1];

            if (triangle[i + 2] < pMin[2]) pMin[2] = triangle[i + 2];
            if (triangle[i + 2] > pMax[2]) pMax[2] = triangle[i + 2];
        }

        return new AABB(pMin, pMax);
    }
}
