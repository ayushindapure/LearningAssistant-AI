package com.learningapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {

    private final List<String> selected = new ArrayList<>();
    private TextView tvCount;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        tvCount = findViewById(R.id.tvCount);
        btnNext = findViewById(R.id.btnNext);
        ChipGroup chipGroup = findViewById(R.id.chipGroupTopics);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        for (String topic : AppData.ALL_TOPICS) {
            Chip chip = new Chip(this);
            chip.setText(topic);
            chip.setCheckable(true);
            chip.setTextColor(Color.WHITE);
            chip.setChipBackgroundColorResource(R.color.blue_card);
            chip.setCheckedIconTint(
                    android.content.res.ColorStateList.valueOf(Color.WHITE));
            chip.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked && selected.size() < 5) {
                    selected.add(topic);
                } else if (isChecked) {
                    chip.setChecked(false); // reject — already at 5
                } else {
                    selected.remove(topic);
                }
                updateCount();
            });
            chipGroup.addView(chip);
        }

        btnNext.setOnClickListener(v -> {
            AppData.get().interests.clear();
            AppData.get().interests.addAll(selected);
            AppData.get().generateTasks();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        updateCount();
    }

    private void updateCount() {
        tvCount.setText(selected.size() + "/5 selected");
        btnNext.setEnabled(!selected.isEmpty());
    }
}
