package com.pyg.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.cart.service.CartService;
import com.pyg.utils.PygResult;
import com.pyg.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    // 查询购物车商品
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String sessionId = getSessionId();
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cartList_sessionId = cartService.findCartListFromRedis(getSessionId());
        if (userId.equals("anonymousUser")){
            return cartList_sessionId;
        } else {
            List<Cart> cartList_userId = cartService.findCartListFromRedis(userId);

            if (cartList_sessionId.size() >0){

                cartList_userId = cartService.mergeCartList(cartList_sessionId,cartList_userId);

                cartService.deleteCartListToRedis(sessionId);

                cartService.saveCartListToRedis(userId,cartList_userId);
            }
            return cartList_userId;
        }

    }

    public String getSessionId() {
        String userKey = CookieUtil.getCookieValue(request, "user-key");
        if (userKey == null) {
            userKey = UUID.randomUUID().toString();
            CookieUtil.setCookie(request, response, "user-key", userKey);
        }
        return userKey;
    }

    // 添加商品
    @RequestMapping("/addGoodsToCartList")
    public PygResult addGoodsToCartList(Long itemId, int num) {
        try {
            // 先获取之前的cartList
            List<Cart> cartList = findCartList();
            // 这个获取添加后cartList,这一步还不放到redis中
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            // 获取sessionId
            String userKey = getSessionId();
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (userId.equals("anonymousUser")) {
                // 往redis添加修改后的cartList
                cartService.saveCartListToRedis(userKey, cartList);
            } else {
                cartService.saveCartListToRedis(userId, cartList);
            }

            return new PygResult(true, "");
        } catch (RuntimeException e) {
            return new PygResult(false, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new PygResult(false, "添加购物车失败");
        }
    }
}
