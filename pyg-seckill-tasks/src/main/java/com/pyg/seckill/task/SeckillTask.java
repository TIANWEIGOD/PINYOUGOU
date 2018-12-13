package com.pyg.seckill.task;

import com.pyg.mapper.TbSeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Scheduled(cron = "30 * * * * *")
    public void initSeckillGoodRedis(){
        redisTemplate.boundValueOps("num").increment(1);
        System.out.println(redisTemplate.boundValueOps("num").increment(0));
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

}
