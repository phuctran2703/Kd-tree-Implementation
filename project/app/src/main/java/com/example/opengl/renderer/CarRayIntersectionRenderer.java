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
import com.example.opengl.helper.AABBHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CarRayIntersectionRenderer extends MeshRender {
    private Car car;
    private AABB box;
    private final float[] rayPosition;
    private final float[] rayDirection;

    public CarRayIntersectionRenderer(Context context, String fileName, float[] rayPosition, float[] rayDirection) {
        this.car = new Car(context, fileName);
        this.box = car.getBoundingBox();
        this.rayPosition = rayPosition;
        this.rayDirection = rayDirection;

        mActivityContext = context;

        setupVertexBuffers();
        setupAABBBuffer();
        setupRayBuffer();
        setupIntersectionPointBuffer();
    }

    private void setupVertexBuffers() {
        float[] vertices = this.car.generate();
        numPoints = vertices.length / (mPositionDataSize + mTextureDataSize);
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    private void setupRayBuffer() {
        float[] rayVertices = {
                rayPosition[0], rayPosition[1], rayPosition[2],
                rayPosition[0] + rayDirection[0] * 1000, rayPosition[1] + rayDirection[1] * 1000, rayPosition[2] + rayDirection[2] * 1000
        };

        mRayBuffer = ByteBuffer.allocateDirect(rayVertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mRayBuffer.put(rayVertices);
        mRayBuffer.position(0);
    }

    private void setupIntersectionPointBuffer() {
        float[] intersectionPoints = new AABBHelper(rayPosition, rayDirection, box.pMin, box.pMax).getIntersectionPoints();
        if (intersectionPoints == null) {
            return;
        }
        mPointBuffer = ByteBuffer.allocateDirect(intersectionPoints.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mPointBuffer.put(intersectionPoints);
        mPointBuffer.position(0);
    }

    private void setupAABBBuffer() {
        float[] AABBArray = this.box.generate();
        mAABBBuffer = ByteBuffer.allocateDirect(AABBArray.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mAABBBuffer.put(AABBArray);
        mAABBBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        super.onSurfaceCreated(glUnused, config);

        final float eyeX = (box.pMin[0] + box.pMax[0]) / 2 + 3;
        final float eyeY = (box.pMin[1] + box.pMax[1]) / 2 + 3;
        final float eyeZ = (box.pMax[2] + box.pMin[2]) / 2 - 10;

        final float lookX = (box.pMin[0] + box.pMax[0]) / 2;
        final float lookY = (box.pMin[1] + box.pMax[1]) / 2;
        final float lookZ = (box.pMin[2] + box.pMax[2]) / 2;

        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[1]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mRayBuffer.capacity() * mBytesPerFloat, mRayBuffer, GLES30.GL_DYNAMIC_DRAW);

        if (mPointBuffer!=null) {
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[2]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mPointBuffer.capacity() * mBytesPerFloat, mPointBuffer, GLES30.GL_DYNAMIC_DRAW);
        }
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        super.onDrawFrame(glUnused);

        long time = SystemClock.uptimeMillis() % 10000L;
        float angle = (360 / 10000.0f) * ((int) time);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);

        super.drawAABB(Colors.GREEN);
        super.drawMesh(Colors.WHITE);
//        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        super.drawRay(10.0f, Colors.WHITE, Colors.BLACK);
        drawPoints(10.0f);
//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

    }

    @Override
    protected void drawPoints(float pointSize) {
        if (mPointBuffer == null) {
            return;
        }

        super.drawPoints(pointSize);

        GLES30.glUniform4fv(mColorHandle, 1, Colors.RED, 0);

        if(mPointBuffer.capacity() == 3) {
            GLES30.glDrawArrays(GLES30.GL_POINTS, 0, mPointBuffer.capacity() / mPositionDataSize);
        } else if (mPointBuffer.capacity() == 6) {
            GLES30.glDrawArrays(GLES30.GL_POINTS, 0, mPointBuffer.capacity() / mPositionDataSize);
            GLES30.glUniform4fv(mColorHandle, 1, Colors.RED, 0);
            GLES30.glDrawArrays(GLES30.GL_LINES, 0, mPointBuffer.capacity() / mPositionDataSize);
        }


        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }
}
