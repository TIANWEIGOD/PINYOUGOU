package com.pyg.pay.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pay.service.WeixinPayService;
import com.pyg.pojo.TbPayLog;
import com.pyg.utils.PygResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping("/createNative")
    public Map createNative() {

         String userId = SecurityContextHolder.getContext().getAuthentication().getName();
         TbPayLog tbPayLog = weixinPayService.searchPayLogFromRedis(userId);

         return weixinPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee() + "");


    }

    @RequestMapping("/queryPayStatus")
    public PygResult queryPayStatus(String out_trade_no) {
        try {
            int time = 0;
            while (time < 10) {
                Thread.sleep(2000);

                Map resultMap = weixinPayService.queryPayStatus(out_trade_no);
                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                    // 修改日志表跟订单表的信息
                    weixinPayService.updateOrderStatus(userId,out_trade_no,resultMap.get("transaction_id")+"");

                    return new PygResult(true, "支付成功");
                }
                System.out.println(time);
                time++;
            }
            return new PygResult(false, "二维码超时");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "支付失败");
        }
    }



}
