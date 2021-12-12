package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = {"com.example.demo"})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@RestController
class MyController {
    @Autowired
    private RedisTemplate redisTemplate;
    private static final Logger log = LoggerFactory.getLogger(MyController.class);

    @RequestMapping("/hello")
    public String test() {
        String result = "";
        ValueOperations<String, String> apiOps = redisTemplate.opsForValue();
        for (int i = 0; i < 1000; i++) {
            apiOps.set(i + "", "world");
        }
        for (int i = 0; i < 1000; i++) {
            result += apiOps.get(i + "");
        }
        System.out.println(result);
        return result;
    }
}
