package com.zizonhyunwoo.springgameserver.user.jwt;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtUserInfo {
    private UUID userId;
    private String email;
    private String nickname;
    private String role;

    public JwtUserInfo(Claims parse) {

        this.userId = UUID.fromString(parse.get("userId", String.class));
        this.email = parse.get("email", String.class);
        this.nickname = parse.get("nickname", String.class);
        this.role = parse.get("role", String.class);
    }
}
