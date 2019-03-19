package com.ask.iba_by.ask;

public class Answer {
    private Integer id;
    private String answer;

    public Answer(Integer id, String answer) {
        this.id = id;
        this.answer = answer;
    }

    public Answer() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
