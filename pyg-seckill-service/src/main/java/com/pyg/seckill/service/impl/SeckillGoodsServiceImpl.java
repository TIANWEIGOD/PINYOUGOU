package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillGoodsExample;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillGoodsService;
import com.pyg.utils.OrderRecode;
import com.pyg.utils.SysContant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import utils.IdWorker;

import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<TbSeckillGoods> findSeckillGoods() {

        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGood").values();

        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {
            TbSeckillGoodsExample example = new TbSeckillGoodsExample();

            TbSeckillGoodsExample.Criteria criteria = example.createCriteria();

            // 商品量要大于0
            criteria.andStockCountGreaterThan(0);
            // 商品开始时间要小于现在的时间
            Date dateNow = new Date();
            criteria.andStartTimeLessThan(dateNow);
            // 商品结束时间要大于现在的时间
            criteria.andEndTimeGreaterThan(dateNow);
            // 商品状态要为正常的状态1
            criteria.andStatusEqualTo("1");

            seckillGoodsList = seckillGoodsMapper.selectByExample(example);

            for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps("seckillGood").put(tbSeckillGoods.getId(), tbSeckillGoods);
                pushSeckillGoodsToRedisList(tbSeckillGoods);
            }
        }
        return seckillGoodsList;
    }

    // 将一个一个数量放到一个list的redis中，key为商品的id
    private void pushSeckillGoodsToRedisList(TbSeckillGoods seckillGoods) {
        Integer stockCount = seckillGoods.getStockCount();

        for (Integer i = 0; i < stockCount; i++) {
            redisTemplate.boundListOps(SysContant.SECKILL_PREFIX + seckillGoods.getId()).leftPush(seckillGoods.getId());
        }
    }

    @Override
    public TbSeckillGoods finOne(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckillGood").get(id);
    }

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Autowired
    private CreateOrder createOrder;

    // 添加订单改(高并发的处理)
    @Override
    public void saveOrder(Long seckillGoodId, String userId) {
        // 先查询redis是否有该商品
        TbSeckillGoods seckillGood = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGood").get(seckillGoodId);

        if (seckillGood == null) {
            throw new RuntimeException("商品已售完");
        }

        // 在查询改用户是否在排队
        Boolean member = redisTemplate.boundSetOps(SysContant.SECKILL_USER).isMember(userId);

        if (member) {
            TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

            if (seckillOrder != null) {
                throw new RuntimeException("你有已下单未支付");
            }
            throw new RuntimeException("您正在排队当中，请稍等");
        }

        // 判断某商品排队量是否超过了承受的压力
        Long increment = redisTemplate.boundValueOps(SysContant.SECKILL_COUNT_GOODSID_PREFIX + seckillGoodId).increment(0);

        if (seckillGood.getStockCount().intValue() + 200 < increment) {
            //超过限制,这里的200可以自定义
            throw new RuntimeException("当前商品抢购人数过多");
        }

        // 都符合
        // 添加排队
        redisTemplate.boundSetOps(SysContant.SECKILL_USER).add(userId);
        // 添加商品抢购人数
        redisTemplate.boundValueOps(SysContant.SECKILL_COUNT_GOODSID_PREFIX + seckillGoodId);
        // 创建下单对象
        OrderRecode orderRecode = new OrderRecode(seckillGoodId, userId);
        // 添加下单缓存
        redisTemplate.boundListOps(OrderRecode.class.getSimpleName()).leftPush(orderRecode);

        System.out.println(1);
        executor.execute(createOrder);
        System.out.println(2);
    }


}
