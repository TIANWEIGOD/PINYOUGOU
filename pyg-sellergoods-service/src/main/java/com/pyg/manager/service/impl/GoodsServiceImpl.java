package com.pyg.manager.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.manager.service.GoodsService;
import com.pyg.mapper.*;
import com.pyg.pojo.TbGoods;
import com.pyg.pojo.TbGoodsDesc;
import com.pyg.pojo.TbGoodsExample;
import com.pyg.pojo.TbItem;
import com.pyg.utils.PageResult;
import com.pyg.vo.Goods;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     *
     * @param goods
     */
    @Override
    public void add(Goods goods) {

        TbGoods tbGoods = goods.getTbGoods();
        tbGoods.setAuditStatus("0");
        tbGoods.setIsMarketable("0");
        tbGoods.setIsDelete("0");
        goodsMapper.insert(tbGoods);

        TbGoodsDesc tbGoodsDesc = goods.getTbGoodsDesc();
        tbGoodsDesc.setGoodsId(tbGoods.getId());
        goodsDescMapper.insert(tbGoodsDesc);

        List<TbItem> itemList = goods.getItemList();
        for (TbItem tbItem : itemList) {
            String title = tbGoods.getGoodsName();
            String spec = tbItem.getSpec(); //{"网络":"移动4G","机身内存":"32G"}
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            for (String key : specMap.keySet()) {
                title += " " + specMap.get(key);
            }
            tbItem.setTitle(title);
            tbItem.setSellPoint(tbGoods.getCaption());
            String itemImages = tbGoodsDesc.getItemImages(); //[{color:,url:}]
            List<Map> itemImageMapList = JSON.parseArray(itemImages, Map.class);
            if (itemImageMapList.size() > 0) {
                String url = (String) itemImageMapList.get(0).get("url");
                tbItem.setImage(url);
            }
            tbItem.setCategoryid(tbGoods.getCategory3Id());
            tbItem.setCreateTime(new Date());
            tbItem.setUpdateTime(new Date());
            tbItem.setGoodsId(tbGoods.getId());
            tbItem.setSellerId(tbGoods.getSellerId());
            tbItem.setCategory(itemCatMapper.selectByPrimaryKey(tbItem.getCategoryid()).getName());
            Long brandId = tbGoods.getBrandId();
            tbItem.setBrand(brandMapper.findBrandById(Integer.parseInt(brandId + "")).getName());
            tbItem.setSeller(sellerMapper.selectByPrimaryKey(tbItem.getSellerId()).getName());
            itemMapper.insert(tbItem);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(TbGoods goods) {
        goodsMapper.updateByPrimaryKey(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbGoods findOne(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            goodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();

        if (goods != null) {
            // 精准查询
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + goods.getSellerId() + "%");
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    // 审核商品
    @Override
    public void updateAuditStatus(Long[] ids, String auditStatus) {
        for (Long id : ids) {
            /*TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(auditStatus);
            goodsMapper.updateByPrimaryKey(tbGoods);*/
            // 浪费性能
            goodsMapper.updateAuditStatus(id, auditStatus);
        }
    }

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("solrItempageUpdate")
    private Destination solrItempageUpdate;

    @Autowired
    @Qualifier("solrItempageDelete")
    private Destination solrItempageDelete;

    // 上下架
    @Override
    public void updateIsMarketable(Long[] ids, String market) {
        for (Long id : ids) {
            goodsMapper.updateIsMarketable(id, market);

            if (market.equals("1")) {
                jmsTemplate.send(solrItempageUpdate, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(id + "");
                    }
                });
            }

            if (market.equals("2")) {
                jmsTemplate.send(solrItempageDelete, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(id + "");
                    }
                });
            }
        }
    }

}
