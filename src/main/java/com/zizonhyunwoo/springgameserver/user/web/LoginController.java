package com.zizonhyunwoo.springgameserver.user.web;

import com.zizonhyunwoo.springgameserver.user.dao.UserDao;
import com.zizonhyunwoo.springgameserver.user.domain.UserEntity;
import com.zizonhyunwoo.springgameserver.user.jwt.JwtUtil;
import com.zizonhyunwoo.springgameserver.user.vo.LoginVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("login")
public class LoginController {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody LoginVO.Login login, HttpServletResponse response){
        System.out.println("login: "+login);
        UserEntity user = userDao.findByEmail(login.getEmail()).orElseThrow(RuntimeException::new);
        if(!encoder.matches(login.getPassword(), user.getPassword()))
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        String token = jwtUtil.generate(user);
        response.setHeader("Authorization", "Bearer " + token);

        return ResponseEntity.ok("OK");
    }
}
