package com.bamboocloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * sss
 *
 * @author luaku
 * @date 2021/11/28
 */

@RestController
@RequestMapping("/redis")
public class RedisController {
@Autowired private RedisTemplate redisTemplate;


    @GetMapping("/add")
    public void addData(){
        redisTemplate.opsForList().set("xxxlx", 1, "value");
    }

}
