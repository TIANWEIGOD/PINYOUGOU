package com.pyg.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.cart.service.CartService;
import com.pyg.mapper.TbItemMapper;
import com.pyg.pojo.TbItem;
import com.pyg.pojo.TbOrderItem;
import com.pyg.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> findCartListFromRedis(String userKey) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userKey);

        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        return cartList;
    }

    @Override
    public void saveCartListToRedis(String userKey, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(userKey, cartList);
    }

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, int num) {
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        if (tbItem == null) {
            throw new RuntimeException("没有此商品");
        }

        // 先判断之前的cartList有没有我这个商品的sellerId
        String sellerId = tbItem.getSellerId();

        Cart cart = findCartFromCartListBySellerId(cartList, sellerId);

        if (cart != null) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            // 判断orderItemList有没有我这个商品通过这个里面有没有商品Sku的id
            TbOrderItem tbOrderItem = findTbOrderItemByOrderItemListByItemId(orderItemList, tbItem.getId());

            if (tbOrderItem != null) {
                tbOrderItem.setNum(tbOrderItem.getNum() + num);
                // 还要修改价格
                double totalFee = tbOrderItem.getNum() * tbOrderItem.getPrice().doubleValue();
                tbOrderItem.setTotalFee(new BigDecimal(totalFee));
                if (tbOrderItem.getNum()<=0) {
                    orderItemList.remove(tbOrderItem);
                    if (orderItemList.size()==0){
                        cartList.remove(cart);
                    }
                }
            } else {
                tbOrderItem = createOrderItem(num, tbItem, new TbOrderItem());
                orderItemList.add(tbOrderItem);
            }

        } else {
            cart = new Cart();
            cart.setSellerId(tbItem.getSellerId());
            cart.setSellerName(tbItem.getSeller());
            // 添加orderItemList
            List<TbOrderItem> orderItemList = new ArrayList<>();
            TbOrderItem tbOrderItem = new TbOrderItem();
            tbOrderItem = createOrderItem(num, tbItem, tbOrderItem);

            orderItemList.add(tbOrderItem);
            cart.setOrderItemList(orderItemList);

            cartList.add(cart);
        }
        return cartList;
    }

    @Override
    public void deleteCartListToRedis(String sessionId) {
        redisTemplate.boundHashOps("cartList").delete(sessionId);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_userId) {
        for (Cart cart : cartList_sessionId) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem tbOrderItem : orderItemList) {
                cartList_userId = addGoodsToCartList(cartList_userId, tbOrderItem.getItemId(), tbOrderItem.getNum());
            }
        }
        return cartList_userId;
    }

    // 查看orderItemList里有没有跟itemId匹配的TbOrderItem
    private TbOrderItem findTbOrderItemByOrderItemListByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (tbOrderItem.getItemId().longValue() == itemId.longValue()){
                return tbOrderItem;
            }
        }
        return null;
    }
    // 查看cartList里有没有跟sellerId匹配的Cart
    private Cart findCartFromCartListBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    private TbOrderItem createOrderItem(int num, TbItem tbItem, TbOrderItem tbOrderItem) {
        tbOrderItem.setNum(num);
        tbOrderItem.setPrice(tbItem.getPrice());
        // 计算总金额
        double totalFee = tbOrderItem.getNum() * tbOrderItem.getPrice().doubleValue();
        tbOrderItem.setTotalFee(new BigDecimal(totalFee));
        // tbOrderItem.setId();  // TODO 插入到mysql时考虑id
        // tbOrderItem.setOrderId(); // TODO 插入到mysql时考虑id

        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setItemId(tbItem.getId());

        return tbOrderItem;
    }


}
