package com.learningapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.util.List;

public class ShareActivity extends AppCompatActivity {

    private ImageView qrImageView;
    private TextView tvShareData;
    private Button btnShareImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        qrImageView = findViewById(R.id.qrImageView);
        tvShareData = findViewById(R.id.tvShareData);
        btnShareImage = findViewById(R.id.btnShareImage);

        generateAndShowQr();
        btnShareImage.setOnClickListener(v -> shareQrCode());
    }

    private void generateAndShowQr() {
        // Build shareable text: user profile + interests + last assessment score (if any)
        AppData data = AppData.get();
        String userName = data.loggedInName.isEmpty() ? "Learner" : data.loggedInName;
        String interests = String.join(", ", data.interests.isEmpty() ?
                List.of("General Programming") : data.interests);

        // Try to get latest history entry from database
        String lastScore = "Not yet";
        try {
            var historyList = com.learningapp.database.HistoryDatabase.getInstance(this)
                    .historyDao().getAllHistory();
            if (!historyList.isEmpty()) {
                lastScore = historyList.get(0).scorePercent + "%";
            }
        } catch (Exception e) {
            lastScore = "N/A";
        }

        String shareText = "LLM Learning AI Profile\n" +
                "Name: " + userName + "\n" +
                "Interests: " + interests + "\n" +
                "Last Score: " + lastScore + "\n" +
                "App: AI-powered learning assistant";

        tvShareData.setText(shareText);

        // Generate QR code
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            BitMatrix bitMatrix = encoder.encode(shareText, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap qrBitmap = encoder.createBitmap(bitMatrix);
            qrImageView.setImageBitmap(qrBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "QR generation failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareQrCode() {
        qrImageView.setDrawingCacheEnabled(true);
        Bitmap qrBitmap = qrImageView.getDrawingCache();
        if (qrBitmap == null) {
            Toast.makeText(this, "Unable to share", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp_qr.png";
        try (java.io.FileOutputStream out = new java.io.FileOutputStream(path)) {
            qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        android.net.Uri uri = androidx.core.content.FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", new java.io.File(path));

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share QR code via"));
    }
}