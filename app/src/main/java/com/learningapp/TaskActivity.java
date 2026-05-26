package com.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class TaskActivity extends AppCompatActivity {

    private AppData.Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        task = AppData.get().currentTask;
        if (task == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvTaskTitle)).setText(task.topic + " Assessment");
        ((TextView) findViewById(R.id.tvTaskDesc)).setText("Complete all questions below");

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        ((Button) findViewById(R.id.btnSubmit)).setOnClickListener(v ->
                startActivity(new Intent(this, ResultsActivity.class)));

        LinearLayout llQ = findViewById(R.id.llQuestions);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int idx = 0; idx < task.questions.size(); idx++) {
            AppData.Question q = task.questions.get(idx);
            int num = idx + 1;

            if ("mcq".equals(q.type)) {
                View v = inflater.inflate(R.layout.item_question_mcq, llQ, false);
                ((TextView) v.findViewById(R.id.tvQuestion)).setText(num + ". " + q.text);

                RadioGroup rg = v.findViewById(R.id.radioGroup);
                for (int oi = 0; oi < q.options.length; oi++) {
                    RadioButton rb = new RadioButton(this);
                    rb.setText(q.options[oi]);
                    rb.setTextColor(0xCCFFFFFF);
                    rb.setTextSize(13f);
                    rb.setPadding(4, 6, 4, 6);
                    final int optIdx = oi;
                    rb.setId(View.generateViewId());
                    rg.addView(rb);
                    if (q.selectedIndex == oi) rb.setChecked(true);
                }
                rg.setOnCheckedChangeListener((group, checkedId) -> {
                    for (int i = 0; i < rg.getChildCount(); i++) {
                        if (rg.getChildAt(i).getId() == checkedId) {
                            q.selectedIndex = i;
                            break;
                        }
                    }
                });

                // ── Hint button ──────────────────────────────────────────────
                Button btnHint = v.findViewById(R.id.btnHint);
                LinearLayout llBox = v.findViewById(R.id.llLlmBox);
                TextView tvPrompt = v.findViewById(R.id.tvLlmPrompt);
                ProgressBar prog = v.findViewById(R.id.progressHint);
                TextView tvResp = v.findViewById(R.id.tvLlmResponse);

                btnHint.setOnClickListener(bv -> {
                    btnHint.setEnabled(false);
                    llBox.setVisibility(View.VISIBLE);
                    prog.setVisibility(View.VISIBLE);
                    tvResp.setVisibility(View.GONE);
                    String prompt = "Give a short 2-3 sentence hint for this question without "
                            + "revealing the answer: \"" + q.text + "\". Topic: " + task.topic;
                    tvPrompt.setText("📝 Prompt: " + prompt);

                    ClaudeApi.ask(prompt, new ClaudeApi.Callback() {
                        @Override public void onSuccess(String text) {
                            prog.setVisibility(View.GONE);
                            tvResp.setText(text);
                            tvResp.setVisibility(View.VISIBLE);
                            btnHint.setText("💡 Hint loaded");
                        }
                        @Override public void onError(String error) {
                            prog.setVisibility(View.GONE);
                            tvResp.setText("⚠ " + error);
                            tvResp.setTextColor(0xFFFF5252);
                            tvResp.setVisibility(View.VISIBLE);
                            btnHint.setEnabled(true);
                        }
                    });
                });

                llQ.addView(v);

            } else { // toggle
                View v = inflater.inflate(R.layout.item_question_toggle, llQ, false);
                ((TextView) v.findViewById(R.id.tvQuestion)).setText(num + ". " + q.text);
                Switch sw = v.findViewById(R.id.switchAnswer);
                TextView tvLabel = v.findViewById(R.id.tvToggleLabel);
                sw.setChecked(q.toggleAnswer);
                tvLabel.setText(q.toggleAnswer ? "Yes, I can" : "Not yet");
                sw.setOnCheckedChangeListener((btn, checked) -> {
                    q.toggleAnswer = checked;
                    tvLabel.setText(checked ? "Yes, I can" : "Not yet");
                });
                llQ.addView(v);
            }
        }
    }
}
