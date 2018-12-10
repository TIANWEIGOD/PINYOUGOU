package com.pyg.order.service;

import com.pyg.pojo.TbOrder;
import com.pyg.pojo.TbPayLog;

public interface OrderService {
    void add(TbOrder tbOrder);

    TbPayLog searchPayLogFromRedis(String userId);

    void updateOrderStatus(String out_trade_no, String transaction_id);
}
