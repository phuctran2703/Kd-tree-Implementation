package com.example.opengl.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;

import com.example.opengl.renderer.CarRayIntersectionRenderer;

public class CarRayIntersectionResult extends AppCompatActivity {
    private Bundle data;
    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);

        data = getIntent().getExtras();
        float ray1X0 = data.getFloat("ray1X0");
        float ray1Y0 = data.getFloat("ray1Y0");
        float ray1Z0 = data.getFloat("ray1Z0");
        float ray1Xd = data.getFloat("ray1Xd");
        float ray1Yd = data.getFloat("ray1Yd");
        float ray1Zd = data.getFloat("ray1Zd");

        float[] ray1Position = new float[]{ray1X0, ray1Y0, ray1Z0};
        float[] ray1Direction = new float[]{ray1Xd, ray1Yd, ray1Zd};

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            mGLSurfaceView.setEGLContextClientVersion(2);

            mGLSurfaceView.setRenderer(new CarRayIntersectionRenderer(this, "laurel.obj", ray1Position, ray1Direction));
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
