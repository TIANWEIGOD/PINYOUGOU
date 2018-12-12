package com.pyg.utils;

import java.io.Serializable;

public class OrderRecode implements Serializable{

    private Long seckillGoodId;

    private String userId;

    public Long getSeckillGoodId() {
        return seckillGoodId;
    }

    public void setSeckillGoodId(Long seckillGoodId) {
        this.seckillGoodId = seckillGoodId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public OrderRecode(Long seckillGoodId, String userId) {

        this.seckillGoodId = seckillGoodId;
        this.userId = userId;
    }
}
