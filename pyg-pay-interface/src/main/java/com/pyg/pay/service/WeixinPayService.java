package com.pyg.pay.service;

import com.pyg.pojo.TbPayLog;

import java.util.Map;

public interface WeixinPayService {

    public Map createNative(String out_trade_no, String total_fee);

    Map queryPayStatus(String out_trade_no) throws Exception;

    TbPayLog searchPayLogFromRedis(String userId);

    void updateOrderStatus(String userId, String out_trade_no, String transaction_id);
}
