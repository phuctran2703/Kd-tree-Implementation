package com.example.opengl.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;

import com.example.opengl.renderer.TwoCarIntersectionRenderer;

public class TwoCarIntersectionResult extends AppCompatActivity {
    private Bundle data;
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);

        data = getIntent().getExtras();
        boolean flag = data.getBoolean("result");

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            mGLSurfaceView.setEGLContextClientVersion(2);

            mGLSurfaceView.setRenderer(new TwoCarIntersectionRenderer(this, "laurel.obj", flag));
        } else {
            return;
        }

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}
