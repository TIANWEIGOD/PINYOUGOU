package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.order.service.OrderService;
import com.pyg.pojo.TbOrder;
import com.pyg.utils.PygResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/add")
    public PygResult add(@RequestBody TbOrder tbOrder){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            // 来自PC端
            tbOrder.setUserId(userId);
            tbOrder.setSourceType("2");
            orderService.add(tbOrder);
            return new PygResult(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false,"添加失败");
        }
    }



}
