package com.zizonhyunwoo.springgameserver.user.filter;

import com.zizonhyunwoo.springgameserver.user.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException
    {
        String accessToken = getToken(request.getCookies(),"access_token");

        if (accessToken!=null && jwtUtil.validate(accessToken)) {
            try {
                Authentication auth = jwtUtil.getAuth(accessToken);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(Cookie[] cookies, String token) {
        if (cookies==null) return null;
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(token))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
