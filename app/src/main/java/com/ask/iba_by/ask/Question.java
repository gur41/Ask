package com.ask.iba_by.ask;

import java.util.ArrayList;

public class Question {
    private Integer id;
    private String question;
    private ArrayList<Answer> answers = new ArrayList<>();

    public Question() {
    }

    public Question(Integer id, String question, ArrayList<Answer> answers) {
        this.id = id;
        this.question = question;
        this.answers = answers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
