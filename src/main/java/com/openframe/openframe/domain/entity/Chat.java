package com.openframe.openframe.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(length = 1000)
    private String response;

    // 생성자를 통해 필드 초기화
    public Chat(String keyword, String response) {
        this.keyword = keyword;
        this.response = response;
    }

    // setChatEntity 메서드 정의
    public Chat setChatEntity(String keyword, String response) {
        return new Chat(keyword, response);
    }
}
