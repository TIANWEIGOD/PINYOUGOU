package com.pyg.seckill.service;

import com.pyg.pojo.TbSeckillOrder;

import java.math.BigDecimal;
import java.util.Map;

public interface SeckillPayService {
    TbSeckillOrder getOrderByUserId(String userId);

    Map<String,String> createNative(Long id, BigDecimal money);

    Map queryPayStatus(String out_trade_no);

    void updateStatus(String userId, Object transaction_id);

    void removeRedisOrder(String userId);
}
