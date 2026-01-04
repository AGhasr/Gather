package org.example.eventregistration.dto;

import java.util.List;

public class CreatePollRequest {
    private String question;
    private List<String> options;

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}