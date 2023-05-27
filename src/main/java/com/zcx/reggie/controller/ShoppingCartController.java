package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.reggie.bean.ShoppingCart;
import com.zcx.reggie.common.BaseContext;
import com.zcx.reggie.common.R;
import com.zcx.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId()).orderByAsc(ShoppingCart::getCreateTime);
        return R.success(shoppingCartService.list(shoppingCartLambdaQueryWrapper));
    }

    /**
     * 添加菜品到购物车
     * @param shoppingCart 物品信息
     * @return 返回添加结果
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("菜品信息：{}", shoppingCart);

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

        ShoppingCart cart = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        // 若购物车中已存在，则数量加一进行更新操作，否则新加一条数据
        if (cart != null) {
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartService.updateById(cart);
        } else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }

        return R.success(cart);
    }

    /**
     * 将一份菜品/套餐移出购物车
     * @param shoppingCart 菜品/套餐信息
     * @return 返回移出（减少）结果
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long userId = BaseContext.getCurrentId();
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId)
                .eq(dishId != null, ShoppingCart::getDishId, dishId)
                .eq(setmealId != null, ShoppingCart::getSetmealId, setmealId);

        ShoppingCart cart = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);

        if (cart != null) {
            int number = cart.getNumber() - 1;

            cart.setNumber(number);
            shoppingCartService.updateById(cart);

            if (number == 0) {
                shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
            }

            return R.success(cart);
        } else return R.error("购物车中没有该菜品/套餐信息");
    }

    /**
     * 清空购物车
     * @return 返回清空结果
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

        return R.success("购物车已清空");
    }
}
