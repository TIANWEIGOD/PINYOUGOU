package com.pyg.utils;

import java.io.Serializable;

public class SysContant implements Serializable {

    // 存储用户排队的key redis set的key
    public static final String SECKILL_USER = "SECKILL_USER_GOODSID_";

    // 存储抢购某个商品+商品id人数的key 这个是redis redis的基础类型的
    public static final String SECKILL_COUNT_GOODSID_PREFIX = "SECKILL_COUNT_PREFIX_GOODSID_";

    // 秒杀商品key的前缀，也要加id 这个是redis list的key
    public static final String SECKILL_PREFIX = "SECKILL_PREFIX_GOODSID_";
}
