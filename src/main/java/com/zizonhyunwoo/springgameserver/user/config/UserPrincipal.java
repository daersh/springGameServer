package com.zizonhyunwoo.springgameserver.user.config;

import com.zizonhyunwoo.springgameserver.user.jwt.JwtUserInfo;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class UserPrincipal extends User {
    private UUID userId;
    private String email;
    private String nickname;
    private String role;

    public UserPrincipal(
            String username,
            @Nullable String password,
            Collection<? extends GrantedAuthority> authorities,
            UUID userId,
            String email,
            String nickname,
            String role
    ) {
        super(username, password, authorities);
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }

    public UserPrincipal(JwtUserInfo jwtUserInfo,List<SimpleGrantedAuthority> authorities) {
        super(jwtUserInfo.getNickname(), "", authorities);
        this.userId = jwtUserInfo.getUserId();
        this.email = jwtUserInfo.getEmail();
        this.nickname = jwtUserInfo.getNickname();
        this.role = jwtUserInfo.getRole();
    }

}
