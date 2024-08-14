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

public class TwoCarIntersectionRenderer extends MeshRender {
    private Car car1;
    private Car car2;
    private AABB box1;
    private AABB box2;
    private boolean isIntersectTwoAABB;

    public TwoCarIntersectionRenderer(Context context, String fileName, boolean isIntersect) {
        this.car1 = new Car(context, fileName);
        this.box1 = car1.getBoundingBox();
        if(isIntersect) this.car2 = transposedCar(car1, new float[]{-1.5f, -1.5f, 0.0f});
        else this.car2 = transposedCar(car1, new float[]{-3f, -3f, 1.0f});
        this.box2 = car2.getBoundingBox();
        mActivityContext = context;
        isIntersectTwoAABB = box1.checkIntersectTwoAABB(box2);
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

        float[] vertices = new float[vertices1.length + vertices2.length];
        System.arraycopy(vertices1, 0, vertices, 0, vertices1.length);
        System.arraycopy(vertices2, 0, vertices, vertices1.length, vertices2.length);

        numPoints = vertices.length / (mPositionDataSize + mTextureDataSize);

        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    private void setupAABBBuffer() {
        float[] AABBArray1 = this.box1.generate();
        float[] AABBArray2 = this.box2.generate();
        float[] AABBArray = new float[AABBArray1.length + AABBArray2.length];
        System.arraycopy(AABBArray1, 0, AABBArray, 0, AABBArray1.length);
        System.arraycopy(AABBArray2, 0, AABBArray, AABBArray1.length, AABBArray2.length);
        mAABBBuffer = ByteBuffer.allocateDirect(AABBArray.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mAABBBuffer.put(AABBArray);
        mAABBBuffer.position(0);
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

        long time = SystemClock.uptimeMillis() % 10000L;
        float angle = (360 / 10000.0f) * ((int) time);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);

        super.drawMesh(Colors.WHITE);
        if (isIntersectTwoAABB) super.drawAABB(Colors.RED);
        else super.drawAABB(Colors.GREEN);
    }
}
