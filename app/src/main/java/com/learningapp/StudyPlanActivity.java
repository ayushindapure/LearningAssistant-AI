package com.learningapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class StudyPlanActivity extends AppCompatActivity {

    private TextView tvPromptLabel, tvPlanResponse, tvPlanError;
    private ProgressBar progressPlan;
    private Button btnRegenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        tvPromptLabel  = findViewById(R.id.tvPromptLabel);
        tvPlanResponse = findViewById(R.id.tvPlanResponse);
        tvPlanError    = findViewById(R.id.tvPlanError);
        progressPlan   = findViewById(R.id.progressPlan);
        btnRegenerate  = findViewById(R.id.btnRegenerate);

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        btnRegenerate.setOnClickListener(v -> fetchPlan());

        fetchPlan();
    }

    private void fetchPlan() {
        List<String> ints = AppData.get().interests;
        String topics = ints.isEmpty() ? "general programming"
                : String.join(", ", ints.subList(0, Math.min(3, ints.size())));

        String prompt = "Create a concise 7-day study plan for a student interested in: "
                + topics + ". Format: Day 1: ..., Day 2: ..., etc. "
                + "Keep it practical and motivating. Max 200 words.";

        tvPromptLabel.setText("📝 Prompt: " + prompt);
        progressPlan.setVisibility(View.VISIBLE);
        tvPlanResponse.setVisibility(View.GONE);
        tvPlanError.setVisibility(View.GONE);
        btnRegenerate.setVisibility(View.GONE);

        ClaudeApi.ask(prompt, new ClaudeApi.Callback() {
            @Override public void onSuccess(String text) {
                progressPlan.setVisibility(View.GONE);
                tvPlanResponse.setText(text);
                tvPlanResponse.setVisibility(View.VISIBLE);
                btnRegenerate.setVisibility(View.VISIBLE);
            }
            @Override public void onError(String error) {
                progressPlan.setVisibility(View.GONE);
                tvPlanError.setText("⚠ " + error);
                tvPlanError.setVisibility(View.VISIBLE);
                btnRegenerate.setVisibility(View.VISIBLE);
            }
        });
    }
}
