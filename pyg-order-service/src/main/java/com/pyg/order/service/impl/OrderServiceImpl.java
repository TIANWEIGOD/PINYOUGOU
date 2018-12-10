package com.pyg.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.mapper.TbOrderItemMapper;
import com.pyg.mapper.TbOrderMapper;
import com.pyg.mapper.TbPayLogMapper;
import com.pyg.order.service.OrderService;
import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbOrderItem;
import com.pyg.pojo.TbPayLog;
import com.pyg.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import utils.IdWorker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private IdWorker idWorker; // 分布式id生成器

    @Override
    public void add(TbOrder tbOrder) {
        String userId = tbOrder.getUserId();
        // 先从redis获取cartList
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);

        // 所有订单id的集合
        List<String> orderIdList = new ArrayList<>();
        // 总金额
        double title_money = 0.00;

        for (Cart cart : cartList) {

            TbOrder order = new TbOrder();

            long orderId = idWorker.nextId();
            // order_id(20) NOT NULL订单id
            order.setOrderId(orderId);
            // payment_type(1) NULL支付类型，1、在线支付，2、货到付款
            order.setPaymentType(tbOrder.getPaymentType());
            // status(1) NULL状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
            order.setStatus("1");
            // create_time NULL订单创建时间
            order.setCreateTime(new Date());
            // update_time NULL订单更新时间
            order.setUpdateTime(new Date());
            // user_id(50) NULL用户id
            order.setUserId(userId);
            // receiver_area_name(100) NULL收货人地区名称(省，市，县)街道
            order.setReceiverAreaName(tbOrder.getReceiverAreaName());
            // receiver_mobile(12) NULL收货人手机
            order.setReceiverMobile(tbOrder.getReceiverMobile());
            // receiver(50) NULL收货人
            order.setReceiver(tbOrder.getReceiver());
            // source_type(1) NULL订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
            order.setSourceType(tbOrder.getSourceType());
            // seller_id(100) NULL商家ID
            order.setSellerId(cart.getSellerId());

            double money = 0.00;
            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            for (TbOrderItem tbOrderItem : orderItemList) {
                tbOrderItem.setId(idWorker.nextId());
                tbOrderItem.setOrderId(orderId);
                tbOrderItem.setSellerId(cart.getSellerId());

                money += tbOrderItem.getTotalFee().doubleValue();

                orderItemMapper.insert(tbOrderItem);
            }
            // payment(20,2) NULL实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分
            order.setPayment(new BigDecimal(money));

            orderMapper.insert(order);

            orderIdList.add(orderId + "");

            title_money += money;
        }

        if ("1".equals(tbOrder.getPaymentType())) {
            addPayLog(userId, orderIdList, title_money);
        }

        redisTemplate.boundHashOps("cartList").delete(userId);

    }

    @Autowired
    private TbPayLogMapper payLogMapper;

    private void addPayLog(String userId, List<String> orderIdList, double title_money) {
        TbPayLog tbPayLog = new TbPayLog();
        // private String outTradeNo;
        tbPayLog.setOutTradeNo(idWorker.nextId() + "");
        // private Date createTime;
        tbPayLog.setCreateTime(new Date());
        // private Long totalFee;
        tbPayLog.setTotalFee((long) (title_money * 100));
        // private String userId;
        tbPayLog.setUserId(userId);
        // private String tradeState; 交易状态
        tbPayLog.setTradeState("0");
        // private String orderList;
        String orderList = orderIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
        tbPayLog.setOrderList(orderList);
        // private String payType;
        tbPayLog.setPayType("1");

        payLogMapper.insert(tbPayLog);

        redisTemplate.boundHashOps("payLog").put(userId,tbPayLog);
        // private Date payTime;
        // private String transactionId;
        // 这俩个是支付完成的时候使用
    }

    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        // 先修改日志
        TbPayLog tbPayLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        // private Date payTime;
        tbPayLog.setPayTime(new Date());
        // private String transactionId;
        tbPayLog.setTransactionId(transaction_id); // 微信支付订单号
        // private String tradeState; 交易状态
        tbPayLog.setTradeState("1"); // 已支付

        // 再通过oderList修改对应的order
        String orderList = tbPayLog.getOrderList();
        String[] orderIds = orderList.split(",");
        // 清楚redis的日志缓存
        for (String orderId : orderIds) {
            TbOrder order = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            if (order != null){
                order.setStatus("2");
                orderMapper.updateByPrimaryKey(order);
            }
        }

        redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());
    }
}
