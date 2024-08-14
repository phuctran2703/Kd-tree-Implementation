package com.example.opengl.renderer;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.os.SystemClock;

import com.example.opengl.Object.Colors;
import com.example.opengl.Object.AABB;
import com.example.opengl.Object.Car;
import com.example.opengl.Object.Vertex;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TwoCarIntervalRenderer extends MeshRender {
    private Car car1;
    private Car car2;
    private AABB box1;
    private AABB box2;
    private final float[] rayDirection;
    private final float interval;
    float[] timeSolution;

    public TwoCarIntervalRenderer(Context context, String fileName, float[] rayDirection, float interval) {
        this.car1 = new Car(context, fileName);
        this.box1 = car1.getBoundingBox();
        this.car2 = transposedCar(car1, new float[]{5.0f, 5.0f, 5.0f});
        this.box2 = car2.getBoundingBox();
        this.rayDirection = rayDirection;
        this.interval = interval;
        mActivityContext = context;
        timeSolution = box1.findIntersectTwoAABBInterval(box2, rayDirection, interval);
        setupVertexBuffers();
        setupAABBBuffer();
    }

    private Car transposedCar(Car originalCar, float[] transposeVector) {
        // Assuming the Car class has a method to get and set its vertices
        float[] originalPosition = originalCar.vertex.positions;
        float[] transposedVertices = new float[originalPosition.length];

        for (int i = 0; i < originalPosition.length; i += 3) {
            transposedVertices[i] = originalPosition[i] + transposeVector[0];
            transposedVertices[i + 1] = originalPosition[i + 1] + transposeVector[1];
            transposedVertices[i + 2] = originalPosition[i + 2] + transposeVector[2];
        }

        // Create a new Car object with transposed vertices
        Car transposedCar = new Car(new Vertex(transposedVertices, originalCar.vertex.normals, originalCar.vertex.textureCoords, originalCar.vertex.colors), originalCar.material);

        return transposedCar;
    }

    private void setupVertexBuffers() {
        float[] vertices1 = this.car1.generate();
        float[] vertices2 = this.car2.generate();

        numPoints = vertices1.length / (mPositionDataSize + mTextureDataSize);
        mVertexBuffer = ByteBuffer.allocateDirect(vertices1.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertices1);
        mVertexBuffer.position(0);

        numPointsMove = vertices2.length / (mPositionDataSize + mTextureDataSize);
        mMovedVertexBuffer = ByteBuffer.allocateDirect(vertices2.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mMovedVertexBuffer.put(vertices2);
        mMovedVertexBuffer.position(0);
    }

    private void setupAABBBuffer() {
        float[] AABBArray1 = this.box1.generate();
        float[] AABBArray2 = this.box2.generate();

        mAABBBuffer = ByteBuffer.allocateDirect(AABBArray1.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mAABBBuffer.put(AABBArray1);
        mAABBBuffer.position(0);

        mAABBMoveBuffer = ByteBuffer.allocateDirect(AABBArray2.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mAABBMoveBuffer.put(AABBArray2);
        mAABBMoveBuffer.position(0);


    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        super.onSurfaceCreated(glUnused, config);

        final float eyeX = (box1.pMin[0] + box1.pMax[0] + box2.pMin[0] + box2.pMax[0]) / 4 +3;
        final float eyeY = (box1.pMin[1] + box1.pMax[1] + box2.pMin[1] + box2.pMax[1]) / 4;
        final float eyeZ = (box1.pMax[2] + box1.pMin[2] + box2.pMax[2] + box2.pMin[2]) /4 -10;

        final float lookX = (box1.pMin[0] + box1.pMax[0] + box2.pMin[0] + box2.pMax[0]) / 4;
        final float lookY = (box1.pMin[1] + box1.pMax[1] + box2.pMin[1] + box2.pMax[1]) / 4;
        final float lookZ = (box1.pMax[2] + box1.pMin[2] + box2.pMax[2] + box2.pMin[2]) /4;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);
        super.drawMesh(Colors.GREEN);
        if (box1.checkIntersectTwoAABB(box2)) super.drawAABB(Colors.RED);
        else super.drawAABB(Colors.GREEN);


        super.onDrawFrame(glUnused);
        if (timeSolution == null) {
            long time = SystemClock.uptimeMillis() % 1000L;
            float t = (interval / 1000.0f) * ((int) time);

            Matrix.setIdentityM(mModelMatrix, 0);
            GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);

            super.drawMesh(Colors.WHITE);
            super.drawAABB(Colors.GREEN);

            Matrix.translateM(mModelMatrix, 0, rayDirection[0]*t, rayDirection[1]*t, rayDirection[2]*t);
            GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);

            super.drawMeshMove(Colors.WHITE);
            super.drawAABBMove(Colors.GREEN);
        }
        else {
            long time = SystemClock.uptimeMillis() % 4000L;
            float t = (interval / 4000.0f) * ((int) time);

            if(t>=timeSolution[0]&& t<=timeSolution[1]){
                Matrix.setIdentityM(mModelMatrix, 0);
                GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
                super.drawMesh(Colors.WHITE);
                super.drawAABB(Colors.RED);

                Matrix.translateM(mModelMatrix, 0, rayDirection[0]*t, rayDirection[1]*t, rayDirection[2]*t);
                GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
                super.drawMeshMove(Colors.WHITE);
                super.drawAABBMove(Colors.RED);
            }
            else {
                Matrix.setIdentityM(mModelMatrix, 0);
                GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
                super.drawMesh(Colors.WHITE);
                super.drawAABB(Colors.GREEN);

                Matrix.translateM(mModelMatrix, 0, rayDirection[0]*t, rayDirection[1]*t, rayDirection[2]*t);
                GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
                super.drawMeshMove(Colors.WHITE);
                super.drawAABBMove(Colors.GREEN);
            }
        }
    }
}
