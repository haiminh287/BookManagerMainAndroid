package com.example.bookmanager.model;

public class CardReviewItem {
    private String content;
    private String created_at;
    private String name;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardReviewItem(String content, String created_at, String name) {
        this.content = content;
        this.created_at = created_at;
        this.name = name;
    }
}
