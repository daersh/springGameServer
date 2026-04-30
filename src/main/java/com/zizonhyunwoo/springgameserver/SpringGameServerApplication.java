package com.zizonhyunwoo.springgameserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringGameServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringGameServerApplication.class, args);
    }

}
