package com.learningapp;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class ClaudeApi {

    // 🔑 Replace with your actual Groq API key from https://console.groq.com
    private static final String API_KEY  = "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";

    // Groq API endpoint (OpenAI compatible)
    private static final String API_URL  = "https://api.groq.com/openai/v1/chat/completions";

    // Free model options: "llama3-8b-8192", "llama3-70b-8192", "mixtral-8x7b-32768", "gemma2-9b-it"
    private static final String MODEL    = "llama-3.1-8b-instant";

    private static final MediaType JSON  = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60,   java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface Callback {
        void onSuccess(String text);
        void onError(String error);
    }

    public static void ask(String prompt, Callback cb) {
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("model", MODEL);
                body.put("max_tokens", 1000);
                body.put("temperature", 0.7);

                JSONArray messages = new JSONArray();
                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", prompt);
                messages.put(userMsg);
                body.put("messages", messages);

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(body.toString(), JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String resBody = response.body() != null ? response.body().string() : "";
                    if (!response.isSuccessful()) {
                        String errorMsg = "API error " + response.code();
                        // Try to parse error details from Groq
                        try {
                            JSONObject errorJson = new JSONObject(resBody);
                            if (errorJson.has("error")) {
                                errorMsg = errorJson.getJSONObject("error").optString("message", errorMsg);
                            }
                        } catch (Exception ignored) {}
                        final String finalError = errorMsg;
                        mainHandler.post(() -> cb.onError(finalError));
                        return;
                    }

                    JSONObject json = new JSONObject(resBody);
                    JSONArray choices = json.getJSONArray("choices");
                    String text = choices.getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                            .trim();
                    mainHandler.post(() -> cb.onSuccess(text));
                }
            } catch (Exception e) {
                mainHandler.post(() -> cb.onError("Network error: " + e.getMessage()));
            }
        }).start();
    }
}