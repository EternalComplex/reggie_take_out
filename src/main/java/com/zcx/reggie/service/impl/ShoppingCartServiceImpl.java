package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.ShoppingCart;
import com.zcx.reggie.common.BaseContext;
import com.zcx.reggie.mapper.ShoppingCartMapper;
import com.zcx.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        // 设置用户id，指定当前菜品是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        // 查询当前菜品或套餐是否已经在购物车中
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        String dishFlavor = shoppingCart.getDishFlavor();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper
                .eq(ShoppingCart::getUserId, userId)
                // 以下eq只会二选一
                .eq(dishId != null, ShoppingCart::getDishId, dishId)  // 添加的是菜品
                .eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);    // 添加的是套餐

        ShoppingCart cart = this.getOne(shoppingCartLambdaQueryWrapper);

        // 若购物车中已存在，则数量加一进行更新操作，否则新加一条数据
        if (cart != null) {
            cart.setNumber(cart.getNumber() + 1);
            this.updateById(cart);
        } else {
            shoppingCart.setNumber(1);
            this.save(shoppingCart);
            cart = shoppingCart;
        }

        return cart;
    }

    @Override
    public ShoppingCart sub(ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId)
                .eq(dishId != null, ShoppingCart::getDishId, dishId)
                .eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);

        ShoppingCart cart = this.getOne(shoppingCartLambdaQueryWrapper);

        if (cart != null) {
            int number = cart.getNumber() - 1;

            cart.setNumber(number);
            this.updateById(cart);

            if (number == 0) {
                this.remove(shoppingCartLambdaQueryWrapper);
            }
        }

        return cart;
    }
}
