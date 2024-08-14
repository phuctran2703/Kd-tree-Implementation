package com.example.opengl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.opengl.R;

public class CarRayIntersectionActivity extends AppCompatActivity {
    private EditText editTextRay1X0, editTextRay1Y0, editTextRay1Z0;
    private EditText editTextRay1Xd, editTextRay1Yd, editTextRay1Zd;
    private Button buttonSubmit, buttonReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_ray_intersection);

        // Ánh xạ các view từ XML
        editTextRay1X0 = findViewById(R.id.editTextRay1X0);
        editTextRay1Y0 = findViewById(R.id.editTextRay1Y0);
        editTextRay1Z0 = findViewById(R.id.editTextRay1Z0);
        editTextRay1Xd = findViewById(R.id.editTextRay1Xd);
        editTextRay1Yd = findViewById(R.id.editTextRay1Yd);
        editTextRay1Zd = findViewById(R.id.editTextRay1Zd);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonReturn = findViewById(R.id.buttonReturn);

        if (savedInstanceState != null) {
            editTextRay1X0.setText(String.valueOf(savedInstanceState.getFloat("ray1X0")));
            editTextRay1Y0.setText(String.valueOf(savedInstanceState.getFloat("ray1Y0")));
            editTextRay1Z0.setText(String.valueOf(savedInstanceState.getFloat("ray1Z0")));
            editTextRay1Xd.setText(String.valueOf(savedInstanceState.getFloat("ray1Xd")));
            editTextRay1Yd.setText(String.valueOf(savedInstanceState.getFloat("ray1Yd")));
            editTextRay1Zd.setText(String.valueOf(savedInstanceState.getFloat("ray1Zd")));
        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float ray1X0 = Float.parseFloat(editTextRay1X0.getText().toString());
                float ray1Y0 = Float.parseFloat(editTextRay1Y0.getText().toString());
                float ray1Z0 = Float.parseFloat(editTextRay1Z0.getText().toString());
                float ray1Xd = Float.parseFloat(editTextRay1Xd.getText().toString());
                float ray1Yd = Float.parseFloat(editTextRay1Yd.getText().toString());
                float ray1Zd = Float.parseFloat(editTextRay1Zd.getText().toString());

                Intent intent = new Intent(CarRayIntersectionActivity.this, CarRayIntersectionResult.class);
                intent.putExtra("ray1X0", ray1X0);
                intent.putExtra("ray1Y0", ray1Y0);
                intent.putExtra("ray1Z0", ray1Z0);
                intent.putExtra("ray1Xd", ray1Xd);
                intent.putExtra("ray1Yd", ray1Yd);
                intent.putExtra("ray1Zd", ray1Zd);
                startActivity(intent);
            }
        });

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save state in Bundle
        outState.putFloat("ray1X0", Float.parseFloat(editTextRay1X0.getText().toString()));
        outState.putFloat("ray1Y0", Float.parseFloat(editTextRay1Y0.getText().toString()));
        outState.putFloat("ray1Z0", Float.parseFloat(editTextRay1Z0.getText().toString()));
        outState.putFloat("ray1Xd", Float.parseFloat(editTextRay1Xd.getText().toString()));
        outState.putFloat("ray1Yd", Float.parseFloat(editTextRay1Yd.getText().toString()));
        outState.putFloat("ray1Zd", Float.parseFloat(editTextRay1Zd.getText().toString()));
    }
}
