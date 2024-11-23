package com.openframe.openframe.api.chat.dto;

public class IndexRequest {
    private String sentence;

    // Add constructors for easier mapping
    public IndexRequest() {}
    public IndexRequest(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
