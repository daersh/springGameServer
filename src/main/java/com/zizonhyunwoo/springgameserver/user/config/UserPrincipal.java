package com.zizonhyunwoo.springgameserver.user.config;

import com.zizonhyunwoo.springgameserver.user.jwt.JwtUserInfo;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class UserPrincipal extends User {
    private final UUID userId;
    private final String email;
    private final String nickname;
    private final String role;

    public UserPrincipal(JwtUserInfo jwtUserInfo,List<SimpleGrantedAuthority> authorities) {
        super(jwtUserInfo.getNickname(), "", authorities);
        this.userId = jwtUserInfo.getUserId();
        this.email = jwtUserInfo.getEmail();
        this.nickname = jwtUserInfo.getNickname();
        this.role = jwtUserInfo.getRole();
    }

}
