package com.learningapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData {

    // ── Singleton ──────────────────────────────────────────────────────────────
    private static AppData instance;
    public static AppData get() {
        if (instance == null) instance = new AppData();
        return instance;
    }

    // ── User ───────────────────────────────────────────────────────────────────
    public String loggedInUser = "";
    public String loggedInName = "";
    public List<String> interests = new ArrayList<>();

    // ── Dummy credentials ──────────────────────────────────────────────────────
    public boolean checkLogin(String username, String password) {
        return "student".equals(username) && "pass123".equals(password);
    }

    // ── Task model ─────────────────────────────────────────────────────────────
    public static class Question {
        public int id;
        public String text;
        public String type;           // "mcq" or "toggle"
        public String[] options;      // only for mcq
        public int correctIndex;      // only for mcq
        public int selectedIndex = -1;
        public boolean toggleAnswer = false;

        public Question(int id, String text, String[] options, int correctIndex) {
            this.id = id; this.text = text; this.type = "mcq";
            this.options = options; this.correctIndex = correctIndex;
        }
        public Question(int id, String text) {
            this.id = id; this.text = text; this.type = "toggle";
        }
    }

    public static class Task {
        public int id;
        public String topic;
        public boolean due;
        public List<Question> questions;

        public Task(int id, String topic, boolean due, List<Question> questions) {
            this.id = id; this.topic = topic;
            this.due = due; this.questions = questions;
        }
    }

    public List<Task> tasks = new ArrayList<>();
    public Task currentTask;

    public void generateTasks() {
        tasks.clear();
        List<String> topics = interests.isEmpty()
                ? Arrays.asList("Algorithms", "Web Development", "Databases")
                : interests.subList(0, Math.min(3, interests.size()));

        for (int i = 0; i < topics.size(); i++) {
            String t = topics.get(i);
            List<Question> qs = new ArrayList<>();
            qs.add(new Question(1,
                    "What is the primary goal of " + t + "?",
                    new String[]{
                        "To optimise and solve computational problems",
                        t + " has no defined purpose",
                        "Only applicable in hardware design",
                        "None of the above"
                    }, 0));
            qs.add(new Question(2,
                    "Can you name a real-world application of " + t + "?"));
            qs.add(new Question(3,
                    "Why is " + t + " important in modern software?",
                    new String[]{
                        "It improves software performance and reliability",
                        "It is only used in academia",
                        "It has been deprecated",
                        "It slows down development"
                    }, 0));
            tasks.add(new Task(i + 1, t, i == 0, qs));
        }
    }

    // ── Available topics ───────────────────────────────────────────────────────
    public static final String[] ALL_TOPICS = {
        "Algorithms", "Data Structures", "Web Development",
        "Testing", "Databases", "Networking",
        "Machine Learning", "Operating Systems",
        "Security", "Cloud Computing"
    };
}
