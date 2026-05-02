package com.zizonhyunwoo.springgameserver.user.web;

import com.zizonhyunwoo.springgameserver.user.config.UserPrincipal;
import com.zizonhyunwoo.springgameserver.user.dao.UserDao;
import com.zizonhyunwoo.springgameserver.user.domain.UserEntity;
import com.zizonhyunwoo.springgameserver.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserDao userDao;
    private final PasswordEncoder encoder;

    @GetMapping("")
    public ResponseEntity<UserVO.Info> getUser(@AuthenticationPrincipal UserPrincipal principal) {
        UserEntity user = userDao.findById(principal.getUserId()).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserVO.Info(user));
    }

    @PostMapping("")
    public ResponseEntity<UserVO.Info> getUser(@RequestBody UserVO.Create newUser) {
        UserEntity user = UserEntity.builder()
                .email(newUser.getEmail())
                .nickname(newUser.getNickname())
                .password(encoder.encode(newUser.getPassword()))
                .role("USER")
                .build();
        userDao.save(user);
        return ResponseEntity.ok(new UserVO.Info(user));
    }

}
