package com.example.trial;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity2 extends AppCompatActivity {

    private TextView heartRateTextView;
    private View redDotView;
    private Handler handler;
    private Runnable heartRateUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        heartRateTextView = findViewById(R.id.heartRateTextView);
        redDotView = findViewById(R.id.redDot);
        handler = new Handler();

        heartRateUpdater = new Runnable() {
            @Override
            public void run() {
                // Generate a random heart rate value between 100 and 120
                int randomHeartRate = new Random().nextInt(21) + 100;
                heartRateTextView.setText("Heart Rate: " + randomHeartRate + " BPM");

                // Update the Y position of the red dot based on heart rate
                int dotPositionY = randomHeartRate * 5; // You can adjust the scaling factor
                redDotView.setY(dotPositionY);

                // Schedule the update every 1000ms (1 second)
                handler.postDelayed(this, 100);
            }
        };

        // Start updating the heart rate and red dot position
        handler.post(heartRateUpdater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the heart rate updating when the activity is destroyed
        handler.removeCallbacks(heartRateUpdater);
    }
}
