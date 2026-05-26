package com.learningapp;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class FlashcardsActivity extends AppCompatActivity {

    private TextView tvTopicLabel, tvPromptLabel, tvCardsResponse, tvCardsError;
    private ProgressBar progressCards;
    private Button btnRegenerate;
    private String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        tvTopicLabel   = findViewById(R.id.tvTopicLabel);
        tvPromptLabel  = findViewById(R.id.tvPromptLabel);
        tvCardsResponse = findViewById(R.id.tvCardsResponse);
        tvCardsError   = findViewById(R.id.tvCardsError);
        progressCards  = findViewById(R.id.progressCards);
        btnRegenerate  = findViewById(R.id.btnRegenerate);

        List<String> ints = AppData.get().interests;
        topic = ints.isEmpty() ? "Algorithms" : ints.get(0);
        tvTopicLabel.setText("✦ Topic: " + topic);

        ((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(v -> finish());
        btnRegenerate.setOnClickListener(v -> fetchCards());

        fetchCards();
    }

    private void fetchCards() {
        String prompt = "Create 3 flashcards for studying \"" + topic + "\".\n"
                + "Format each as:\nCard 1:\nQ: [question]\nA: [answer]\n\n"
                + "Card 2:\nQ: ...\nA: ...\n\nCard 3:\nQ: ...\nA: ...\n\n"
                + "Keep answers concise (1-2 sentences).";

        tvPromptLabel.setText("📝 Prompt: " + prompt);
        progressCards.setVisibility(View.VISIBLE);
        tvCardsResponse.setVisibility(View.GONE);
        tvCardsError.setVisibility(View.GONE);
        btnRegenerate.setVisibility(View.GONE);

        ClaudeApi.ask(prompt, new ClaudeApi.Callback() {
            @Override public void onSuccess(String text) {
                progressCards.setVisibility(View.GONE);
                tvCardsResponse.setText(text);
                tvCardsResponse.setVisibility(View.VISIBLE);
                btnRegenerate.setVisibility(View.VISIBLE);
            }
            @Override public void onError(String error) {
                progressCards.setVisibility(View.GONE);
                tvCardsError.setText("⚠ " + error);
                tvCardsError.setVisibility(View.VISIBLE);
                btnRegenerate.setVisibility(View.VISIBLE);
            }
        });
    }
}
