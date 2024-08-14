package com.example.opengl.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;

public class TextureLoader {
    public static int loadTexture(Context context, int resourceId) {
        final int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error generating texture handle.");
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // No pre-scaling

        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            throw new RuntimeException("Error loading texture: Bitmap is null.");
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        return textureHandle[0];
    }

    public static int createDefaultTexture() {
        int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);

            // Create a 1x1 white pixel texture
            ByteBuffer buffer = ByteBuffer.allocateDirect(4);
            buffer.put((byte) 255);
            buffer.put((byte) 255);
            buffer.put((byte) 255);
            buffer.put((byte) 255);
            buffer.flip();

            GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 1, 1, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        } else {
            throw new RuntimeException("Error creating texture.");
        }

        return textureHandle[0];
    }
}
