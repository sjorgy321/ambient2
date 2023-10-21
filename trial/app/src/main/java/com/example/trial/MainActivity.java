package com.example.trial;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnNavigate = findViewById(R.id.btnNavigate);
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to navigate to the new activity
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }

}
