package com.example.opengl.renderer;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.os.SystemClock;
import android.util.Log;

import com.example.opengl.Object.Colors;
import com.example.opengl.Object.AABB;
import com.example.opengl.Object.Car;
import com.example.opengl.Object.CarComponent;
import com.example.opengl.Object.KDNode;
import com.example.opengl.Object.Ray;
import com.example.opengl.helper.KDNodeHelper;
import com.example.opengl.helper.carComponentHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CarMultiRayIntersectRenderer extends MeshRender {
    private Car car;
    private AABB box;
    private Ray[] rays;

    protected final int mPointInTriangleNumber = 3;

    public CarMultiRayIntersectRenderer(Context context, String fileName, int numberOfRays) {
        this.car = new Car(context, fileName);
        this.box = car.getBoundingBox();
        this.rays = new Ray[numberOfRays];
        for (int i = 0; i < numberOfRays; i++) {
            this.rays[i] = new Ray(new float[]{0.0f, 5.0f, -5.0f}, new float[]{-0.7f+(float)i/5, -1.0f, 1.0f});
        }
        mActivityContext = context;

        setupVertexBuffers();
        setupAABBBuffer();
        setupRayBuffer();
        setupIntersectedVertexBuffers();
    }

    private void setupVertexBuffers() {
        float[] vertices = this.car.generate();
        numPoints = vertices.length / (mPositionDataSize + mTextureDataSize);
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);
    }

    private void setupIntersectedVertexBuffers() {
        long startTime = System.currentTimeMillis();

        CarComponent[] components = CarComponent.getCarComponents(car);
        KDNode rootNode= KDNode.build(car.getBoundingBox(), components);
        CarComponent[] intersectedComponents  = new KDNodeHelper(rootNode).getIntersectionWithMultipleRay(rays);

//        CarComponent[] intersectedComponents  = new carComponentHelper(car).getIntersectionWithMultipleRay(rays);

        long endTime = System.currentTimeMillis();

        long elapsedTime = endTime - startTime;
        Log.d("Executed time", ""+ elapsedTime);

        float[] vertices = new float[intersectedComponents .length * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber];
        for (int i = 0; i < intersectedComponents .length; i++) {
            CarComponent component = intersectedComponents[i];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber] = component.triangle[0];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 1] = component.triangle[1];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 2] = component.triangle[2];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 3] = component.textureCoords[0];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 4] = component.textureCoords[1];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 5] = component.triangle[3];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 6] = component.triangle[4];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 7] = component.triangle[5];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 8] = component.textureCoords[2];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 9] = component.textureCoords[3];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 10] = component.triangle[6];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 11] = component.triangle[7];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 12] = component.triangle[8];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 13] = component.textureCoords[4];
            vertices[i * (mPositionDataSize+mTextureDataSize) * mPointInTriangleNumber + 14] = component.textureCoords[5];
        }
        mIntersectedVertexBuffers = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mIntersectedVertexBuffers.put(vertices);
        mIntersectedVertexBuffers.position(0);

    }

    private void setupRayBuffer() {
        float[] rayVertices = new float[rays.length * mPositionDataSize * 2];
        for (int i = 0; i < rays.length; i++) {
            rayVertices[i * mPositionDataSize * 2] = rays[i].rayPosition[0];
            rayVertices[i * mPositionDataSize * 2 + 1] = rays[i].rayPosition[1];
            rayVertices[i * mPositionDataSize * 2 + 2] = rays[i].rayPosition[2];
            rayVertices[i * mPositionDataSize * 2 + 3] = rays[i].rayPosition[0] + rays[i].rayDirection[0] * 1000;
            rayVertices[i * mPositionDataSize * 2 + 4] = rays[i].rayPosition[1] + rays[i].rayDirection[1] * 1000;
            rayVertices[i * mPositionDataSize * 2 + 5] = rays[i].rayPosition[2] + rays[i].rayDirection[2] * 1000;
        }

        mRayBuffer = ByteBuffer.allocateDirect(rayVertices.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mRayBuffer.put(rayVertices);
        mRayBuffer.position(0);
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
//        Matrix.rotateM(mModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);

        super.drawAABB(Colors.GREEN);
        drawIntersectedMesh();

        super.drawMesh(Colors.WHITE);
//        GLES30.glDisable(GLES30.GL_DEPTH_TEST);
        super.drawRay(10.0f, Colors.WHITE, Colors.BLACK);
//        drawPoints(10.0f);
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

    protected void drawIntersectedMesh() {
        if (mIntersectedVertexBuffers == null) {
            return;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureDataHandle);
        GLES30.glUniform1i(mTextureHandle, 0);

        GLES30.glUseProgram(mProgramHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[4]);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false, (mPositionDataSize+mTextureDataSize) * mBytesPerFloat, 0);
        GLES30.glVertexAttribPointer(mTexCoordinateHandle, mTextureDataSize, GLES30.GL_FLOAT, false, (mPositionDataSize+mTextureDataSize) * mBytesPerFloat, mPositionDataSize* mBytesPerFloat);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glEnableVertexAttribArray(mTexCoordinateHandle);

        GLES30.glUniform4fv(mColorHandle, 1, Colors.RED, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mIntersectedVertexBuffers.capacity() / (mPositionDataSize+mTextureDataSize));

        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mTexCoordinateHandle);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureDefaultDataHandle);
    }
}
