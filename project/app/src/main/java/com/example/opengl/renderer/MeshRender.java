package com.example.opengl.renderer;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.opengl.R;
import com.example.opengl.helper.TextureLoader;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class MeshRender implements GLSurfaceView.Renderer {
    protected final float[] mModelMatrix = new float[16];
    protected final float[] mViewMatrix = new float[16];
    protected final float[] mProjectionMatrix = new float[16];
    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mMovedVertexBuffer;
    protected FloatBuffer mIntersectedVertexBuffers;
    protected FloatBuffer mAABBBuffer;
    protected FloatBuffer mAABBMoveBuffer;
    protected FloatBuffer mRayBuffer;
    protected FloatBuffer mPointBuffer;

    protected final int[] mVBOHandles = new int[5];

    protected int mProgramHandle;

    protected int mPositionHandle;
    protected int mTexCoordinateHandle;
    protected int mTextureDataHandle;
    protected int mTextureDefaultDataHandle;
    protected int mPointSizeHandle;
    protected int mColorHandle;
    protected int mTextureHandle;

    protected int mModelMatrixHandle;
    protected int mViewMatrixHandle;
    protected int mProjectionMatrixHandle;

    protected int numPoints;
    protected int numPointsMove;
    protected Context mActivityContext;
    protected final int mBytesPerFloat = 4;
    protected final int mPositionDataSize = 3;
    protected final int mTextureDataSize = 2;

    protected String getVertexShader() {
        return
                "uniform mat4 u_ModelMatrix;\n" +
                "uniform mat4 u_ViewMatrix;\n" +
                "uniform mat4 u_ProjectionMatrix;\n" +
                "uniform float u_PointSize;\n" +
                "attribute vec4 a_Position;\n" +
                "attribute vec2 a_TexCoordinate; \n" +
                "varying vec2 v_TexCoordinate; \n" +
                "void main() {\n" +
                "   mat4 u_MVPMatrix = u_ProjectionMatrix * u_ViewMatrix * u_ModelMatrix;\n" +
                "   gl_Position = u_MVPMatrix * a_Position;\n" +
                "   gl_PointSize = u_PointSize;\n" +
                "   v_TexCoordinate = a_TexCoordinate;\n" +
                "}\n";
    }
    
    protected String getFragmentShader() {
        return
                "precision mediump float;\n" +
                "varying vec2 v_TexCoordinate; \n" +

                "uniform sampler2D u_Texture; \n" +
                "uniform vec4 u_Color;\n" +
                "void main() {\n" +
                "    gl_FragColor = u_Color * texture2D(u_Texture, v_TexCoordinate);\n" +
                "}\n";
    }

    private void setupProgram() {
        final String vertexShader = getVertexShader();
        int vertexShaderHandle = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
        GLES30.glShaderSource(vertexShaderHandle, vertexShader);
        GLES30.glCompileShader(vertexShaderHandle);

        final String fragmentShader = getFragmentShader();
        int fragmentShaderHandle = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
        GLES30.glShaderSource(fragmentShaderHandle, fragmentShader);
        GLES30.glCompileShader(fragmentShaderHandle);

        mProgramHandle = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgramHandle, vertexShaderHandle);
        GLES30.glAttachShader(mProgramHandle, fragmentShaderHandle);
        GLES30.glBindAttribLocation(mProgramHandle, 0, "a_Position");
        GLES30.glBindAttribLocation(mProgramHandle, 1, "a_TexCoordinate");
        GLES30.glLinkProgram(mProgramHandle);

        mPositionHandle = GLES30.glGetAttribLocation(mProgramHandle, "a_Position");
        mTexCoordinateHandle = GLES30.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
        mPointSizeHandle = GLES30.glGetUniformLocation(mProgramHandle, "u_PointSize");
        mColorHandle = GLES30.glGetUniformLocation(mProgramHandle, "u_Color");
        mTextureHandle = GLES30.glGetUniformLocation(mProgramHandle, "u_Texture");
        mModelMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle, "u_ModelMatrix");
        mViewMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle, "u_ViewMatrix");
        mProjectionMatrixHandle = GLES30.glGetUniformLocation(mProgramHandle, "u_ProjectionMatrix");

        GLES30.glUseProgram(mProgramHandle);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

//         GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        setupProgram();

        GLES30.glGenBuffers(5, mVBOHandles, 0);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * mBytesPerFloat, mVertexBuffer, GLES30.GL_DYNAMIC_DRAW);

        if(mMovedVertexBuffer != null){
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[1]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mMovedVertexBuffer.capacity() * mBytesPerFloat, mMovedVertexBuffer, GLES30.GL_DYNAMIC_DRAW);
        }
        if(mAABBMoveBuffer != null){
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[2]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mAABBMoveBuffer.capacity() * mBytesPerFloat, mAABBMoveBuffer, GLES30.GL_DYNAMIC_DRAW);
        }
        if(mAABBBuffer != null){
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[3]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mAABBBuffer.capacity() * mBytesPerFloat, mAABBBuffer, GLES30.GL_DYNAMIC_DRAW);
        }
        if(mIntersectedVertexBuffers != null){
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[4]);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mIntersectedVertexBuffers.capacity() * mBytesPerFloat, mIntersectedVertexBuffers, GLES30.GL_DYNAMIC_DRAW);
        }

        // Bind the texture to this unit.
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        mTextureDataHandle = TextureLoader.loadTexture(mActivityContext, R.drawable.image);
        mTextureDefaultDataHandle = TextureLoader.createDefaultTexture();
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureDefaultDataHandle);
        GLES30.glUniform1i(mTextureHandle, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES30.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 20.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(mModelMatrix, 0);

        GLES30.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
        GLES30.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);
        GLES30.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0);
    }

    protected void drawPoints(float pointSize){
        GLES30.glUseProgram(mProgramHandle);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[2]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mPointBuffer.capacity() * mBytesPerFloat, mPointBuffer, GLES30.GL_DYNAMIC_DRAW);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false, mPositionDataSize * mBytesPerFloat, 0);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        GLES30.glUniform1f(mPointSizeHandle, pointSize);
    }

    protected void drawMesh(float[] meshColor) {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureDataHandle);

        GLES30.glUseProgram(mProgramHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[0]);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false, (mPositionDataSize+mTextureDataSize) * mBytesPerFloat, 0);
        GLES30.glVertexAttribPointer(mTexCoordinateHandle, mTextureDataSize, GLES30.GL_FLOAT, false, (mPositionDataSize+mTextureDataSize) * mBytesPerFloat, mPositionDataSize* mBytesPerFloat);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glEnableVertexAttribArray(mTexCoordinateHandle);

        GLES30.glUniform4fv(mColorHandle, 1, meshColor, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, numPoints);

        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mTexCoordinateHandle);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureDefaultDataHandle);
    }

    protected void drawMeshMove(float[] meshColor) {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureDataHandle);

        GLES30.glUseProgram(mProgramHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[1]);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false, (mPositionDataSize+mTextureDataSize) * mBytesPerFloat, 0);
        GLES30.glVertexAttribPointer(mTexCoordinateHandle, mTextureDataSize, GLES30.GL_FLOAT, false, (mPositionDataSize+mTextureDataSize) * mBytesPerFloat, mPositionDataSize* mBytesPerFloat);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glEnableVertexAttribArray(mTexCoordinateHandle);

        GLES30.glUniform4fv(mColorHandle, 1, meshColor, 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, numPoints);

        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mTexCoordinateHandle);

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTextureDefaultDataHandle);
    }

    protected void drawRay(float sourcePointSize, float[] sourcePointColor, float[] rayColor) {
        GLES30.glUseProgram(mProgramHandle);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[1]);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false, mPositionDataSize * mBytesPerFloat, 0);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        GLES30.glUniform1f(mPointSizeHandle, sourcePointSize);
        GLES30.glUniform4fv(mColorHandle, 1, sourcePointColor, 0);
        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1);

        GLES30.glUniform4fv(mColorHandle, 1, rayColor, 0);
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, mRayBuffer.capacity() / mPositionDataSize);

        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    protected void drawAABB(float[] AABBColor) {
        GLES30.glUseProgram(mProgramHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[3]);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false, mPositionDataSize * mBytesPerFloat, 0);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        GLES30.glUniform4fv(mColorHandle, 1, AABBColor, 0);
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, mAABBBuffer.capacity() / mPositionDataSize);

        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }

    protected void drawAABBMove(float[] AABBColor) {
        GLES30.glUseProgram(mProgramHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBOHandles[2]);
        GLES30.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES30.GL_FLOAT, false, mPositionDataSize * mBytesPerFloat, 0);
        GLES30.glEnableVertexAttribArray(mPositionHandle);

        GLES30.glUniform4fv(mColorHandle, 1, AABBColor, 0);
        GLES30.glDrawArrays(GLES30.GL_LINES, 0, mAABBBuffer.capacity() / mPositionDataSize);

        GLES30.glDisableVertexAttribArray(mPositionHandle);
    }
}