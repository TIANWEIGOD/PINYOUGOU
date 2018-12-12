package com.pyg.seckill.service;

import com.pyg.pojo.TbSeckillGoods;

import java.util.List;

public interface SeckillGoodsService {
    List<TbSeckillGoods> findSeckillGoods();

    TbSeckillGoods finOne(Long id);

    void saveOrder(Long id, String userId);
}
