package com.example.opengl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.opengl.Object.KDNode;
import com.example.opengl.activity.CarMultiRayIntersectActivity;
import com.example.opengl.activity.CarRayIntersectionActivity;
import com.example.opengl.activity.TwoCarIntersectionActivity;
import com.example.opengl.activity.TwoCarIntervalResult;

public class MainActivity extends AppCompatActivity {
    Button carIntersectRayButton, carIntersectMultiRay, twoCarIntersect, twoCarInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        carIntersectRayButton = findViewById(R.id.CarIntersectRay);
        carIntersectMultiRay = findViewById(R.id.CarIntersectMultiRay);
        twoCarIntersect = findViewById(R.id.twoCarIntersect);
        twoCarInterval = findViewById(R.id.twoCarInterval);

        carIntersectRayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CarRayIntersectionActivity.class);
                startActivity(intent);
            }
        });

        carIntersectMultiRay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CarMultiRayIntersectActivity.class);
                startActivity(intent);
            }
        });

        twoCarIntersect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TwoCarIntersectionActivity.class);
                startActivity(intent);
            }
        });

        twoCarInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TwoCarIntervalResult.class);
                startActivity(intent);
            }
        });
    }
}

//public class MainActivity extends AppCompatActivity {
//    private GLSurfaceView mGLSurfaceView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mGLSurfaceView = new GLSurfaceView(this);
//
//        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
//        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
//
//        if (supportsEs2)
//        {
//            mGLSurfaceView.setEGLContextClientVersion(2);
//            mGLSurfaceView.setRenderer(new TwoCarIntervalRenderer(this,"laurel.obj", new float[]{-1.0f, -1.0f, -1.0f}, 10));
//        }
//        else
//        {
//            return;
//        }
//
//        setContentView(mGLSurfaceView);
//    }
//    protected void onResume()
//    {
//        super.onResume();
//        mGLSurfaceView.onResume();
//    }
//
//    @Override
//    protected void onPause()
//    {
//        super.onPause();
//        mGLSurfaceView.onPause();
//    }
//}