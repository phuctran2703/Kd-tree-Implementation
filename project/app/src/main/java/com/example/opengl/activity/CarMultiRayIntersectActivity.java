package com.example.opengl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.opengl.R;

public class CarMultiRayIntersectActivity extends AppCompatActivity {
    private EditText editTextNumOfRays;
    private Button buttonSubmit, buttonReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_multi_ray_intersection);

        // Map the views from XML
        editTextNumOfRays = findViewById(R.id.editTextNumOfRays);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonReturn = findViewById(R.id.buttonReturn);

        // Set up button click listeners
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numOfRaysStr = editTextNumOfRays.getText().toString();
                if (numOfRaysStr.isEmpty()) {
                    Toast.makeText(CarMultiRayIntersectActivity.this, "Please enter the number of rays", Toast.LENGTH_SHORT).show();
                    return;
                }

                int numOfRays;
                try {
                    numOfRays = Integer.parseInt(numOfRaysStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(CarMultiRayIntersectActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Proceed to the next activity
                Intent intent = new Intent(CarMultiRayIntersectActivity.this, CarMultiRayIntersectResult.class);
                intent.putExtra("numOfRays", numOfRays);
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
        outState.putString("numOfRays", editTextNumOfRays.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state from Bundle
        editTextNumOfRays.setText(savedInstanceState.getString("numOfRays"));
    }
}
