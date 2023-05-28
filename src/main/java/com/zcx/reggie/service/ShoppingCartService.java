package com.zcx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.reggie.bean.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {
    ShoppingCart add(ShoppingCart shoppingCart);

    ShoppingCart sub(ShoppingCart shoppingCart);
}
