package com.pyg.pay.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.order.service.OrderService;
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

    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        // IdWorker idWorker = new IdWorker();
        // long out_trade_no = idWorker.nextId();
        // return weixinPayService.createNative(out_trade_no + "", "1");
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(userId);

        return weixinPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee() + "");

    }

    @RequestMapping("/queryPayStatus")
    public PygResult queryPayStatus(String out_trade_no) {

        int count = 0;

        while (true) {
            Map map = weixinPayService.queryPayStatus(out_trade_no);

            if (map == null) {
                return new PygResult(false, "支付失败");
            }

            if (map.get("trade_state").equals("SUCCESS")) {
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id") + "");
                return new PygResult(true, "支付成功");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            count++;

            if (count >= 10) {
                return new PygResult(false, "二维码超时");
            }

        }
    }
}
