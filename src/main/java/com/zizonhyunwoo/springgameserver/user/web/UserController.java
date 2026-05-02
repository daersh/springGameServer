package com.zizonhyunwoo.springgameserver.user.web;

import com.zizonhyunwoo.springgameserver.user.dao.UserDao;
import com.zizonhyunwoo.springgameserver.user.domain.UserEntity;
import com.zizonhyunwoo.springgameserver.user.vo.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserDao userDao;

    @GetMapping("/{userEmail}")
    public ResponseEntity<UserInfo.Info> getUser(@PathVariable String userEmail) {
        UserEntity user = userDao.findByEmail(userEmail).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new UserInfo.Info(user));
    }

    @PostMapping("")
    public ResponseEntity<UserInfo.Info> getUser(@RequestBody UserEntity user) {
        System.out.println(user);
        userDao.save(user);
        return ResponseEntity.ok(new UserInfo.Info(user));
    }

}
