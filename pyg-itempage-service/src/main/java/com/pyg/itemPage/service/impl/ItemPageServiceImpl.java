package com.pyg.itemPage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.itemPage.service.ItemPageService;
import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbItemExample;
import com.pyg.vo.Goods;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public Goods findOne(Long goodsId) {
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goodsId);
        List<TbItem> itemList = itemMapper.selectByExample(example);

        Goods goods = new Goods();
        goods.setTbGoods(tbGoods);
        goods.setTbGoodsDesc(tbGoodsDesc);
        goods.setItemList(itemList);
        return goods;
    }

    @Override
    public List<Goods> findAll() {
        List<Goods> list = new ArrayList<>();
        List<TbGoods> tbGoods = goodsMapper.selectByExample(null);
        for (TbGoods tbGood : tbGoods) {
            Goods goods = findOne(tbGood.getId());
            list.add(goods);
        }
        return list;
    }
}
