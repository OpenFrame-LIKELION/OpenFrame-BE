package com.openframe.openframe.api.chat.dto;

public class MemoRequest {
    private String content;
    private String type;

    // Add constructors for easier mapping
    public MemoRequest() {}
    public MemoRequest(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
