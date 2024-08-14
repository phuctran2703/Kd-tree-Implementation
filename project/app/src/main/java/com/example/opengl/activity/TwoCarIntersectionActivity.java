package com.example.opengl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.opengl.R;

public class TwoCarIntersectionActivity extends AppCompatActivity {
    private Button buttonIntersect, buttonNotIntersect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_car_intersection);

        // Map the views from XML
        buttonIntersect = findViewById(R.id.intersection);
        buttonNotIntersect = findViewById(R.id.notIntersection);

        // Set up button click listeners
        buttonIntersect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the intersection logic or navigate to the result activity
                Intent intent = new Intent(TwoCarIntersectionActivity.this, TwoCarIntersectionResult.class);
                intent.putExtra("result", true);
                startActivity(intent);
            }
        });

        buttonNotIntersect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the not intersection logic or navigate to the result activity
                Intent intent = new Intent(TwoCarIntersectionActivity.this, TwoCarIntersectionResult.class);
                intent.putExtra("result", false);
                startActivity(intent);
            }
        });
    }
}
