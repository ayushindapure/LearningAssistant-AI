package com.learningapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.learningapp.database.HistoryDatabase;
import com.learningapp.database.HistoryEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout llHistoryList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        llHistoryList = findViewById(R.id.llHistoryList);

        loadHistoryAsync();
    }

    private void loadHistoryAsync() {
        // Show loading indicator (optional)
        TextView loadingMsg = new TextView(this);
        loadingMsg.setText("Loading history...");
        loadingMsg.setTextColor(0xCCFFFFFF);
        loadingMsg.setTextSize(14f);
        loadingMsg.setPadding(0, 40, 0, 0);
        loadingMsg.setGravity(android.view.Gravity.CENTER);
        llHistoryList.addView(loadingMsg);

        // Run database query on background thread
        new Thread(() -> {
            List<HistoryEntry> history = HistoryDatabase.getInstance(this)
                    .historyDao().getAllHistory();

            // Update UI on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                llHistoryList.removeAllViews(); // remove loading message

                if (history.isEmpty()) {
                    TextView empty = new TextView(this);
                    empty.setText("📭 No past assessments yet.\nComplete a quiz to see history.");
                    empty.setTextColor(0xCCFFFFFF);
                    empty.setTextSize(14f);
                    empty.setPadding(0, 40, 0, 0);
                    empty.setGravity(android.view.Gravity.CENTER);
                    llHistoryList.addView(empty);
                    return;
                }

                LayoutInflater inflater = LayoutInflater.from(this);
                for (HistoryEntry entry : history) {
                    View item = inflater.inflate(R.layout.item_history, llHistoryList, false);
                    TextView tvTopic = item.findViewById(R.id.tvHistoryTopic);
                    TextView tvScore = item.findViewById(R.id.tvHistoryScore);
                    TextView tvDate = item.findViewById(R.id.tvHistoryDate);

                    tvTopic.setText(entry.topic);
                    tvScore.setText(entry.scorePercent + "%");
                    tvDate.setText(dateFormat.format(new Date(entry.timestamp)));

                    llHistoryList.addView(item);
                }
            });
        }).start();
    }
}