package com.zizonhyunwoo.springgameserver.user.jwt;

import com.zizonhyunwoo.springgameserver.user.config.UserPrincipal;
import com.zizonhyunwoo.springgameserver.user.domain.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.key}")
    private String jwtKey;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(UserEntity user) {
        Date now = new Date();
        long expireTime = 360000;
        return Jwts.builder()
                .claims(
                        Jwts.claims()
                                .add("userId", user.getId())
                                .add("email",user.getEmail())
                                .add("nickname",user.getNickname())
                                .add("role", user.getRole())
                                .build()
                )
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expireTime))
                .signWith(key)
                .compact();
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }

    public JwtUserInfo getJwtUserInfo(String token){
        return new JwtUserInfo(parse(token));
    }

    public boolean validate(String token){
        try {
            parse(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 클레임 문자열이 비어 있습니다.");
        } catch (Exception e) {
            log.error("JWT 토큰 검증 중 알 수 없는 오류가 발생했습니다.", e);
        }
        return false;
    }

    public Authentication getAuth(String token){
        JwtUserInfo jwtUserInfo = new JwtUserInfo(parse(token));
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_"+jwtUserInfo.getRole()));
        return new UsernamePasswordAuthenticationToken(new UserPrincipal(jwtUserInfo, authorities), null, authorities);
    }
}
