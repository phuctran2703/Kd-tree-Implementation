package com.example.opengl.Object;

import android.util.Log;

import com.example.opengl.helper.AABBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class KDNode {
    public AABB coverBoundingBox;
    public CarComponent[] carComponents;
    public int splitAxis;
    public float splitValue;
    public KDNode leftNode;
    public KDNode rightNode;
    public static int maxDepth;
    public static int maxCarComponents;

    public KDNode(AABB coverBoundingBox, CarComponent[] carComponents, int splitAxis, float splitValue, KDNode leftNode, KDNode rightNode) {
        this.coverBoundingBox = coverBoundingBox;
        this.carComponents = carComponents;
        this.splitAxis = splitAxis;
        this.splitValue = splitValue;
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }

    public static KDNode build(AABB coverAABB, CarComponent[] carComponents) {
        maxDepth = (int) (8+1.3*Math.log10(carComponents.length));
        maxCarComponents = 5;

        KDNode rootNode = buildKdTree(coverAABB, carComponents, 0);
        return rootNode;
    }

    public static KDNode buildKdTree(AABB coverAABB, CarComponent[] carComponents, int depth) {
        if (carComponents.length == 0) {
            return null;
        }
        if (carComponents.length <= maxCarComponents || depth >= maxDepth) {
            return new KDNode(coverAABB, carComponents, 3, 0, null, null);
        }

        float[] d = new float[]{coverAABB.pMax[0] - coverAABB.pMin[0], coverAABB.pMax[1] - coverAABB.pMin[1], coverAABB.pMax[2] - coverAABB.pMin[2]};
        Float invTotalSA = 1.f / (2.f * (d[0] * d[1] + d[0] * d[2] + d[1] * d[2]));

        float bestCost = Float.MAX_VALUE;
        int bestAxis = 0;
        float bestSplitValue = 0;
        int bestCountLeft = 0;
        int bestCountRight = 0;

        // Loop through each axis to find the best split axis and value
        for (int axis = 0; axis < 3; axis++) {
            final int splitAxis = axis;
            Arrays.sort(carComponents, Comparator.comparingDouble(carComponent ->
                    (carComponent.aabb.pMin[splitAxis] + carComponent.aabb.pMax[splitAxis]) / 2
            ));
            for (int i = 0; i < carComponents.length - 1; i++) {
                float splitValue = (carComponents[i].aabb.pMax[axis] + carComponents[i+1].aabb.pMin[axis]) / 2;
                int countLeft = 0;
                int countRight = 0;
                for (CarComponent carComponent : carComponents) {
                    if (carComponent.aabb.pMax[axis] <= splitValue) countLeft++;
                    else if (carComponent.aabb.pMin[axis] >= splitValue) countRight++;
                    else {
                        countLeft++;
                        countRight++;
                    }
                }
                AABB leftcoverAABB = new AABB(coverAABB.pMin.clone(), coverAABB.pMax.clone());
                leftcoverAABB.pMax[axis] = splitValue;
                AABB rightcoverAABB = new AABB(coverAABB.pMin.clone(), coverAABB.pMax.clone());
                rightcoverAABB.pMin[axis] = splitValue;

                float cost = (surfaceArea(leftcoverAABB) * countLeft + surfaceArea(rightcoverAABB) * countRight) * invTotalSA;

                if (cost < bestCost) {
                    bestCost = cost;
                    bestAxis = axis;
                    bestSplitValue = splitValue;
                    bestCountLeft = countLeft;
                    bestCountRight = countRight;
                }
            }
        }

        // Allocate arrays for left and right child nodes
        CarComponent[] leftBoxes = new CarComponent[bestCountLeft];
        CarComponent[] rightBoxes = new CarComponent[bestCountRight];
        int leftIndex = 0;
        int rightIndex = 0;

        // Populate the left and right child node arrays
        for (CarComponent carComponent : carComponents) {
            if (carComponent.aabb.pMax[bestAxis] <= bestSplitValue) {
                leftBoxes[leftIndex++] = carComponent;
            } else if (carComponent.aabb.pMin[bestAxis] >= bestSplitValue) {
                rightBoxes[rightIndex++] = carComponent;
            } else {
                leftBoxes[leftIndex++] = carComponent;
                rightBoxes[rightIndex++] = carComponent;
            }
        }


        AABB leftcoverAABB = new AABB(coverAABB.pMin.clone(), coverAABB.pMax.clone());
        leftcoverAABB.pMax[bestAxis] = bestSplitValue;
        AABB rightcoverAABB = new AABB(coverAABB.pMin.clone(), coverAABB.pMax.clone());
        rightcoverAABB.pMin[bestAxis] = bestSplitValue;


        KDNode node = new KDNode(coverAABB, null, bestAxis, bestSplitValue, null, null);
        node.leftNode = buildKdTree(leftcoverAABB, leftBoxes, depth + 1);
        node.rightNode = buildKdTree(rightcoverAABB, rightBoxes, depth + 1);

        return node;
    }


    public void insert(CarComponent newComponent, int depth) {
        if (this.carComponents != null) {
            CarComponent[] newCarComponents = Arrays.copyOf(this.carComponents, this.carComponents.length + 1);
            newCarComponents[newCarComponents.length - 1] = newComponent;
            this.carComponents = newCarComponents;

            if (this.carComponents.length > maxCarComponents) {
                KDNode newSubtree = build(this.coverBoundingBox, this.carComponents);
                this.copyFrom(newSubtree);
            }
            return;
        }

        float[] d = new float[]{coverBoundingBox.pMax[0] - coverBoundingBox.pMin[0], coverBoundingBox.pMax[1] - coverBoundingBox.pMin[1], coverBoundingBox.pMax[2] - coverBoundingBox.pMin[2]};

        int axis;
        if (d[0] >= d[1] && d[0] >= d[2]) axis = 0;
        else if (d[1] >= d[2]) axis = 1;
        else axis = 2;

        float[] pMin = newComponent.aabb.pMin;
        float[] pMax = newComponent.aabb.pMax;

        if (pMax[axis] <= this.splitValue) {
            if (this.leftNode == null) {
                AABB newLeftAABB = new AABB(this.coverBoundingBox.pMin.clone(), this.coverBoundingBox.pMax.clone());
                newLeftAABB.pMax[axis] = this.splitValue;
                this.leftNode = new KDNode(newLeftAABB, new CarComponent[]{newComponent}, 4, 0, null, null);
            } else this.leftNode.insert(newComponent, depth + 1);
        } else if (pMin[axis] > this.splitValue) {
            if (this.rightNode == null) {
                AABB newRightAABB = new AABB(this.coverBoundingBox.pMin.clone(), this.coverBoundingBox.pMax.clone());
                newRightAABB.pMin[axis] = this.splitValue;
                this.rightNode = new KDNode(newRightAABB, new CarComponent[]{newComponent}, 4, 0, null, null);
            } else this.rightNode.insert(newComponent, depth + 1);
        } else {
            if (this.leftNode == null) {
                AABB newLeftAABB = new AABB(this.coverBoundingBox.pMin.clone(), this.coverBoundingBox.pMax.clone());
                newLeftAABB.pMax[axis] = this.splitValue;
                this.leftNode = new KDNode(newLeftAABB, new CarComponent[]{newComponent}, 4, 0, null, null);
            } else this.leftNode.insert(newComponent, depth + 1);
            if (this.rightNode == null) {
                AABB newRightAABB = new AABB(this.coverBoundingBox.pMin.clone(), this.coverBoundingBox.pMax.clone());
                newRightAABB.pMin[axis] = this.splitValue;
                this.rightNode = new KDNode(newRightAABB, new CarComponent[]{newComponent}, 4, 0, null, null);
            } else this.rightNode.insert(newComponent, depth + 1);
        }
    }

    private static float surfaceArea(AABB aabb) {
        float[] d = new float[]{aabb.pMax[0] - aabb.pMin[0], aabb.pMax[1] - aabb.pMin[1], aabb.pMax[2] - aabb.pMin[2]};
        return (2.f * (d[0]*d[1] + d[0]*d[2] + d[1]*d[2]));
    }

    private void copyFrom(KDNode newNode) {
        this.coverBoundingBox = newNode.coverBoundingBox;
        this.carComponents = newNode.carComponents;
        this.splitAxis = newNode.splitAxis;
        this.splitValue = newNode.splitValue;
        this.leftNode = newNode.leftNode;
        this.rightNode = newNode.rightNode;
    }
}
