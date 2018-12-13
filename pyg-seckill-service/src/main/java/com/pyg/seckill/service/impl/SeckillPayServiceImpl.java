package com.pyg.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.mapper.TbSeckillGoodsMapper;
import com.pyg.mapper.TbSeckillOrderMapper;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillPayService;
import com.pyg.utils.SysContant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.HttpClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class SeckillPayServiceImpl implements SeckillPayService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;

    @Override
    public TbSeckillOrder getOrderByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    // 查询生成二维码
    @Override
    public Map<String, String> createNative(Long out_trade_no, BigDecimal total_fee) {

        Map paramMap = new HashMap();

        paramMap.put("appid", appid);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", "品优购");
        paramMap.put("out_trade_no", out_trade_no + "");
        // paramMap.put("total_fee", (total_fee.doubleValue() * 100) + "");
        paramMap.put("total_fee", "1");
        paramMap.put("trade_type", "NATIVE"); // 交易类型
        paramMap.put("spbill_create_ip", "127.0.0.1"); // 终端IP
        paramMap.put("notify_url", notifyurl); // 通知会掉地址可以随便写

        try {
            // 生成sign签名并且map转换成xml格式
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

            httpClient.setXmlParam(signedXml);

            httpClient.post();

            String content = httpClient.getContent();
            // 返回的xml转换成map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            resultMap.put("out_trade_no", out_trade_no + "");
            resultMap.put("total_fee", (total_fee.doubleValue() * 100) + "");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // 监听是否成功
    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map paramMap = new HashMap();

        paramMap.put("appid", appid);
        paramMap.put("mch_id", partner);
        paramMap.put("out_trade_no", out_trade_no);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());

        try {
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");

            httpClient.setXmlParam(signedXml);
            httpClient.post();

            String content = httpClient.getContent();

            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Override
    public void updateStatus(String userId, Object transaction_id) {
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

        if (seckillOrder == null) {
            throw new RuntimeException("订单不存在或异常");
        }

        seckillOrder.setPayTime(new Date());
        seckillOrder.setStatus("1"); // 订单支付
        seckillOrder.setTransactionId(transaction_id + "");

        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
        // 支付成功删除用户排队状态
        redisTemplate.boundSetOps(SysContant.SECKILL_USER).remove(userId);
        // 该订单也跟着删除
        redisTemplate.boundHashOps("seckillOrder").delete(userId);

    }

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    // 超时删除订单的方法
    @Override
    public void removeRedisOrder(String userId) {
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);

        Long goodId = seckillOrder.getSeckillId();

        BoundHashOperations hashOperations = redisTemplate.boundHashOps("seckillGood");
        TbSeckillGoods seckillGood = (TbSeckillGoods) hashOperations.get(goodId);

        if (seckillGood == null) {
            seckillGood = seckillGoodsMapper.selectByPrimaryKey(goodId);
            seckillGood.setStockCount(0);
        }

        seckillGood.setStockCount(seckillGood.getStockCount() + 1);
        // 也往队列出添加该商品，不添加的话，我们都是从这里拿的商品，这样就会导致商品的数量出现问题
        redisTemplate.boundListOps(SysContant.SECKILL_PREFIX + goodId).leftPush(goodId);
        redisTemplate.boundHashOps("seckillGood").put(goodId, seckillGood);

        redisTemplate.boundHashOps("seckillOrder").delete(userId);
        seckillOrder.setStatus("2"); // 订单超时失效状态
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
        // 用户排队删除
        redisTemplate.boundSetOps(SysContant.SECKILL_USER).remove(userId);
    }


}
