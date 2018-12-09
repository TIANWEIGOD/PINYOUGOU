package com.pyg.cart.service;

import com.pyg.vo.Cart;

import java.util.List;

public interface CartService {
    List<Cart> findCartListFromRedis(String userKey);


    void saveCartListToRedis(String userKey, List<Cart> cartList);

    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, int num);

    void deleteCartListToRedis(String sessionId);

    List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_userId);
}
