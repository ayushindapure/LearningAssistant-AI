package com.learningapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        AppData.Task task = AppData.get().currentTask;
        if (task == null) { finish(); return; }

        // ── Score ────────────────────────────────────────────────────────────
        int total   = (int) task.questions.stream().filter(q -> "mcq".equals(q.type)).count();
        int correct = (int) task.questions.stream()
                .filter(q -> "mcq".equals(q.type) && q.selectedIndex == q.correctIndex).count();
        int pct = total > 0 ? Math.round(correct * 100f / total) : 0;

        // Save to history database (NEW)
        if (task != null && total > 0) {
            com.learningapp.database.HistoryEntry entry = new com.learningapp.database.HistoryEntry(
                    task.topic,
                    pct,
                    System.currentTimeMillis()
            );
            com.learningapp.database.HistoryDatabase.getInstance(this).historyDao().insert(entry);
        }

        ((TextView) findViewById(R.id.tvScorePercent)).setText(pct + "%");
        ((TextView) findViewById(R.id.tvScoreDetail)).setText(correct + "/" + total + " correct");

        // ── Results list ──────────────────────────────────────────────────────
        LinearLayout llResults = findViewById(R.id.llResults);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int idx = 0; idx < task.questions.size(); idx++) {
            AppData.Question q = task.questions.get(idx);
            int num = idx + 1;

            // Container
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = dpToPx(2);
            item.setLayoutParams(lp);
            item.setPadding(0, dpToPx(10), 0, dpToPx(10));

            // Divider
            View div = new View(this);
            LinearLayout.LayoutParams dlp =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1));
            div.setLayoutParams(dlp);
            div.setBackgroundColor(0x1AFFFFFF);
            item.addView(div);

            // Question label
            TextView tvQ = new TextView(this);
            tvQ.setText(num + ". " + q.text);
            tvQ.setTextColor(0xBBFFFFFF);
            tvQ.setTextSize(13f);
            tvQ.setPadding(0, dpToPx(8), 0, dpToPx(4));
            item.addView(tvQ);

            if ("mcq".equals(q.type)) {
                // Correct answer
                String ans = (q.options != null && q.correctIndex < q.options.length)
                        ? q.options[q.correctIndex] : "N/A";
                TextView tvAns = new TextView(this);
                tvAns.setText("✓ " + ans);
                tvAns.setTextColor(0xFF69F0AE);
                tvAns.setTextSize(13f);
                item.addView(tvAns);

                // LLM box
                LinearLayout llmBox = new LinearLayout(this);
                llmBox.setOrientation(LinearLayout.VERTICAL);
                llmBox.setBackgroundResource(R.drawable.llm_box_bg);
                llmBox.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));
                LinearLayout.LayoutParams blp =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                blp.topMargin = dpToPx(8);
                llmBox.setLayoutParams(blp);
                llmBox.setVisibility(View.GONE);

                TextView tvPrompt = new TextView(this);
                tvPrompt.setTextColor(0x99FFFFFF);
                tvPrompt.setTextSize(11f);
                tvPrompt.setTypeface(android.graphics.Typeface.MONOSPACE);
                llmBox.addView(tvPrompt);

                ProgressBar prog = new ProgressBar(this);
                prog.setVisibility(View.GONE);
                llmBox.addView(prog);

                TextView tvResp = new TextView(this);
                tvResp.setTextColor(Color.WHITE);
                tvResp.setTextSize(13f);
                tvResp.setLineSpacing(0, 1.5f);
                tvResp.setVisibility(View.GONE);
                llmBox.addView(tvResp);

                item.addView(llmBox);

                // Explain button
                Button btnExplain = new Button(this);
                btnExplain.setText("🔍 Explain Answer");
                btnExplain.setTextColor(0xFF69F0AE);
                btnExplain.setTextSize(12f);
                btnExplain.setBackgroundResource(R.drawable.btn_small_bg);
                LinearLayout.LayoutParams elp =
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                elp.topMargin = dpToPx(6);
                btnExplain.setLayoutParams(elp);
                btnExplain.setContentDescription("Explain why this answer is correct");
                item.addView(btnExplain);

                final String chosen = (q.options != null && q.selectedIndex >= 0
                        && q.selectedIndex < q.options.length)
                        ? q.options[q.selectedIndex] : "No answer selected";

                btnExplain.setOnClickListener(bv -> {
                    btnExplain.setEnabled(false);
                    llmBox.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.VISIBLE);
                    tvResp.setVisibility(View.GONE);

                    String prompt = "In 2-3 sentences, explain why \"" + ans
                            + "\" is the correct answer to: \"" + q.text
                            + "\". The student chose: \"" + chosen
                            + "\". Topic: " + task.topic
                            + ". Be encouraging and educational.";
                    tvPrompt.setText("📝 Prompt: " + prompt);

                    ClaudeApi.ask(prompt, new ClaudeApi.Callback() {
                        @Override public void onSuccess(String text) {
                            prog.setVisibility(View.GONE);
                            tvResp.setText(text);
                            tvResp.setVisibility(View.VISIBLE);
                            btnExplain.setText("✓ Explained");
                        }
                        @Override public void onError(String error) {
                            prog.setVisibility(View.GONE);
                            tvResp.setText("⚠ " + error);
                            tvResp.setTextColor(0xFFFF5252);
                            tvResp.setVisibility(View.VISIBLE);
                            btnExplain.setEnabled(true);
                        }
                    });
                });

            } else {
                TextView tvAns = new TextView(this);
                tvAns.setText("Practice more on this topic");
                tvAns.setTextColor(0xBBFFFFFF);
                tvAns.setTextSize(13f);
                item.addView(tvAns);
            }

            llResults.addView(item);
        }

        // ── Continue ─────────────────────────────────────────────────────────
        ((Button) findViewById(R.id.btnContinue)).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}