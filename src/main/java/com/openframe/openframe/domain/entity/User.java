package com.openframe.openframe.domain.entity;

import com.openframe.openframe.security.oauth.dto.OidcDecodePayload;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String subId;
    private String name;
    private String picture;
    private String email;

    public static User createUser (OidcDecodePayload oidcDecodePayload) {

        User user = new User();
        user.subId = oidcDecodePayload.sub();
        user.name = oidcDecodePayload.nickname();
        user.email = oidcDecodePayload.email();
        user.picture = oidcDecodePayload.picture();
        return user;
    }

}
