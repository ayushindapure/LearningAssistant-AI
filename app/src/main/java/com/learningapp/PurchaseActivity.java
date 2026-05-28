package com.learningapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PurchaseActivity extends AppCompatActivity {

    private EditText etCardNumber, etExpiry, etCvv;
    private Button btnBuy;
    private TextView tvPremiumStatus, tvMockWarning;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);
        btnBuy = findViewById(R.id.btnBuy);
        tvPremiumStatus = findViewById(R.id.tvPremiumStatus);
        tvMockWarning = findViewById(R.id.tvMockWarning);

        tvMockWarning.setText("⚠️ Mock payment – no real charge. Use any test numbers (e.g. 4242 4242 4242 4242).");

        updatePremiumStatus();

        btnBuy.setOnClickListener(v -> processPurchase());
    }

    private void updatePremiumStatus() {
        boolean isPremium = prefs.getBoolean("isPremium", false);
        if (isPremium) {
            tvPremiumStatus.setText("✅ You are a PREMIUM member! Unlimited AI features.");
            btnBuy.setText("Already Premium");
            btnBuy.setEnabled(false);
        } else {
            tvPremiumStatus.setText("⭐ Free plan – upgrade to unlock advanced explanations & priority AI.");
            btnBuy.setText("Upgrade to Premium – $4.99");
            btnBuy.setEnabled(true);
        }
    }

    private void processPurchase() {
        String card = etCardNumber.getText().toString().replaceAll("\\s", "");
        String expiry = etExpiry.getText().toString();
        String cvv = etCvv.getText().toString();

        // very basic validation – demo purpose
        if (card.length() >= 13 && expiry.length() >= 4 && cvv.length() >= 3) {
            // Simulate network call
            Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show();
            btnBuy.postDelayed(() -> {
                prefs.edit().putBoolean("isPremium", true).apply();
                updatePremiumStatus();
                Toast.makeText(PurchaseActivity.this, "🎉 Purchase successful! Welcome to Premium.", Toast.LENGTH_LONG).show();
            }, 1500);
        } else {
            Toast.makeText(this, "Please enter valid card details", Toast.LENGTH_SHORT).show();
        }
    }
}