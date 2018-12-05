package com.pyg.itemPage.service;

import com.pyg.vo.Goods;

import java.util.List;

public interface ItemPageService {
    Goods findOne(Long goodsId);


    List<Goods> findAll();
}
