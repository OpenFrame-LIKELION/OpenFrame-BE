package com.openframe.openframe.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Memo> memos = new ArrayList<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Index> indices = new ArrayList<>();

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Memo> getMemos() {
        return memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }

    public List<Index> getIndices() {
        return indices;
    }

    public void setIndices(List<Index> indices) {
        this.indices = indices;
    }
}
