package com.pyg.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbSeckillGoods;
import com.pyg.seckill.service.SeckillGoodsService;
import com.pyg.utils.PygResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGood")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;


    @RequestMapping("/findSeckillGoods")
    public List<TbSeckillGoods> findSeckillGoods() {
        return seckillGoodsService.findSeckillGoods();
    }

    @RequestMapping("/findOne")
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsService.finOne(id);
    }

    @RequestMapping("/addOrder")
    public PygResult addOrder(Long id) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(userId)) {
            try {
                seckillGoodsService.saveOrder(id,userId);
                return new PygResult(true, "添加成功");
            } catch (RuntimeException e) {
                e.printStackTrace();
                return new PygResult(false, e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return new PygResult(false, "添加失败");
            }
        } else {
            return new PygResult(false,"用户没有登录");
        }
    }



}
