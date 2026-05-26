package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ImageButton btnBack = findViewById(R.id.btnBack);
        Button btnCreate = findViewById(R.id.btnCreateAccount);

        btnBack.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            // In a real app, validate + register. Here we proceed with dummy data.
            AppData.get().loggedInUser = "student";
//            AppData.get().loggedInName = "Alex Johnson";
            AppData.get().loggedInName = "Ayush Indapure";
            startActivity(new Intent(this, InterestsActivity.class));
        });
    }
}
