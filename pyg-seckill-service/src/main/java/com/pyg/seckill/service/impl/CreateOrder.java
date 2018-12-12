package com.pyg.seckill.service.impl;

import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.utils.OrderRecode;
import com.pyg.utils.SysContant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import utils.IdWorker;

import java.util.Date;

@Component
public class CreateOrder implements Runnable {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Override
    public void run() {
        OrderRecode orderRecode = (OrderRecode) redisTemplate.boundListOps(OrderRecode.class.getSimpleName()).rightPop();

        if (orderRecode != null) {

            // 从商品redisList拿商品
            Long goodId = (Long) redisTemplate.boundListOps(SysContant.SECKILL_PREFIX + orderRecode.getSeckillGoodId()).rightPop();

            if (goodId == null) {
                // 商品已经售完，清楚该用户排队信息
                redisTemplate.boundSetOps(SysContant.SECKILL_USER).remove(orderRecode.getUserId());
            }

            // 获取商品完整对象
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGood").get(goodId);

            if (seckillGoods != null) {
                TbSeckillOrder seckillOrder = buildSeckillOrder(orderRecode.getUserId(), seckillGoods);
                redisTemplate.boundHashOps("seckillOrder").put(orderRecode.getUserId(), seckillOrder);

                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                Integer stockCount = seckillGoods.getStockCount();

                if (stockCount <= 0) {
                    seckillGoodsMapper.updateByPrimaryKey(seckillGoods);

                    redisTemplate.boundHashOps("seckillGood").delete(seckillGoods.getId());
                }

                redisTemplate.boundHashOps("seckillGood").put(seckillGoods.getId(), seckillGoods);

                // 下单成功后该商品抢购人数-1
                redisTemplate.boundValueOps(SysContant.SECKILL_COUNT_GOODSID_PREFIX + seckillGoods.getId()).increment(-1);
            }

        }
    }

    private TbSeckillOrder buildSeckillOrder(String userId, TbSeckillGoods seckillGood) {
        TbSeckillOrder order = new TbSeckillOrder();
        long orderId = idWorker.nextId();
        order.setId(orderId); // 主键订单号
        order.setSeckillId(seckillGood.getId()); // 商品id
        order.setMoney(seckillGood.getCostPrice());// 支付金额
        order.setUserId(userId); // 用户id
        order.setSeckillId(Long.parseLong("1"));
        // create_timedatetime NULL创建时间
        order.setCreateTime(new Date());
        // statusvarchar(1) NULL状态 0 未支付 1 支付
        order.setStatus("0");
        // seller_idvarchar(50) NULL商家
        // pay_timedatetime NULL支付时间
        return order;
    }

    // 添加订单原始版本
    public void addOrder(Long id, String userId) {

        // 先从redis查询看是否有有添加的商品
        TbSeckillGoods seckillGood = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGood").get(id);

        // 查看库存是否还有
        Integer stockCount = seckillGood.getStockCount();

        if (seckillGood == null || stockCount <= 0) {
            throw new RuntimeException("商品已经销售完毕");
        }

        // 先下单
        TbSeckillOrder order = buildSeckillOrder(userId, seckillGood);
        redisTemplate.boundHashOps("seckillOrder").put(userId, order);
        // 后商品同步redis中的数据
        seckillGood.setStockCount(stockCount - 1);
        if (seckillGood.getStockCount() == 0) {
            // 现将秒杀商品同步到数据库中这样就可以必秒redis跟数据空中的不一致
            seckillGoodsMapper.updateByPrimaryKey(seckillGood);
            // 再讲redis中的数据删除
            redisTemplate.boundHashOps("seckillGood").delete(id);
        }

        redisTemplate.boundHashOps("seckillGood").put(id, seckillGood);
    }
}
