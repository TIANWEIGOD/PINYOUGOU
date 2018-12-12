package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSeckillOrder;
import com.pyg.seckill.service.SeckillPayService;
import com.pyg.utils.PygResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/seckillPay")
public class SeckillPayController {

    @Reference
    private SeckillPayService seckillPayService;

    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println(userId);

        TbSeckillOrder seckillOrder = seckillPayService.getOrderByUserId(userId);

        return seckillPayService.createNative(seckillOrder.getId(), seckillOrder.getMoney());
    }

    @RequestMapping("/queryPayStatus")
    public PygResult queryPayStatus(String out_trade_no) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        int num = 0;

        try {
            while (num < 20) {
                Thread.sleep(2000);

                Map resultMap = seckillPayService.queryPayStatus(out_trade_no);

                if ("SUCCESS".equals(resultMap.get("trade_state"))) {
                    // transaction_id微信支付订单号
                    seckillPayService.updateStatus(userId, resultMap.get("transaction_id"));
                    return new PygResult(true, "支付成功");
                }
                System.out.println(num);
                num++;
            }

            // seckillPayService.removeRedisOrder(userId);
            return new PygResult(false, "验证码已经超时");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "支付失败");
        }
    }
}
