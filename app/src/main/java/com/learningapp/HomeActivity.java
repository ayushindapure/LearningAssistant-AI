package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tvName  = findViewById(R.id.tvUserName);
        TextView tvBadge = findViewById(R.id.tvDueBadge);
        LinearLayout llList = findViewById(R.id.llTaskList);

        tvName.setText(AppData.get().loggedInName);

        List<AppData.Task> tasks = AppData.get().tasks;
        long dueCount = tasks.stream().filter(t -> t.due).count();
        tvBadge.setText("📋 You have " + dueCount + " task" + (dueCount != 1 ? "s" : "") + " due");

        // ── Task cards ───────────────────────────────────────────────────────
        LayoutInflater inflater = LayoutInflater.from(this);
        for (AppData.Task task : tasks) {
            View card = inflater.inflate(R.layout.item_task_card, llList, false);
            ((TextView) card.findViewById(R.id.tvTaskTitle)).setText(task.topic + " Assessment");
            ((TextView) card.findViewById(R.id.tvTaskDesc)).setText(
                    task.questions.size() + " questions on " + task.topic);
            Switch sw = card.findViewById(R.id.switchDue);
            sw.setChecked(task.due);
            sw.setOnCheckedChangeListener((v, checked) -> task.due = checked);

            card.setOnClickListener(v -> {
                AppData.get().currentTask = task;
                startActivity(new Intent(this, TaskActivity.class));
            });
            llList.addView(card);
        }



        // ── AI Study Plan tile ───────────────────────────────────────────────
        View planTile = buildAiTile(inflater,
                "🗓 AI Study Plan",
                "Generate a personalized 7-day plan based on your interests");
        planTile.setOnClickListener(v ->
                startActivity(new Intent(this, StudyPlanActivity.class)));
        llList.addView(planTile);

        // ── AI Flashcards tile ───────────────────────────────────────────────
        String topic = AppData.get().interests.isEmpty() ? "Algorithms" : AppData.get().interests.get(0);
        View cardsTile = buildAiTile(inflater,
                "🃏 AI Flashcards",
                "Create flashcards from your top topic: " + topic);
        cardsTile.setOnClickListener(v ->
                startActivity(new Intent(this, FlashcardsActivity.class)));
        llList.addView(cardsTile);
    }

    private View buildAiTile(LayoutInflater inflater, String title, String desc) {
        View tile = inflater.inflate(R.layout.item_task_card, (ViewGroup) null, false);
        tile.setBackground(getDrawable(R.drawable.card_bg));
        TextView tvAiTag = tile.findViewById(R.id.tvAiTag);
        tvAiTag.setText("✦ AI Utility");
        ((TextView) tile.findViewById(R.id.tvTaskTitle)).setText(title);
        ((TextView) tile.findViewById(R.id.tvTaskDesc)).setText(desc);
        tile.findViewById(R.id.switchDue).setVisibility(View.GONE);
        return tile;
    }
}
