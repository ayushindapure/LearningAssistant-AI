package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private TextView tvError, tvNeedAccount;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername   = findViewById(R.id.etUsername);
        etPassword   = findViewById(R.id.etPassword);
        tvError      = findViewById(R.id.tvError);
        btnLogin     = findViewById(R.id.btnLogin);
        tvNeedAccount = findViewById(R.id.tvNeedAccount);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvNeedAccount.setOnClickListener(v ->
                startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void attemptLogin() {
        String u = etUsername.getText().toString().trim();
        String p = etPassword.getText().toString();

        if (AppData.get().checkLogin(u, p)) {
            AppData.get().loggedInUser = u;
            AppData.get().loggedInName = "Ayush Indapure";
            AppData.get().generateTasks();
            tvError.setVisibility(View.GONE);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            tvError.setText("Invalid username or password.");
            tvError.setVisibility(View.VISIBLE);
        }
    }
}
