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

        ShoppingCart cart = shoppingCartService.add(shoppingCart);

        // 需返回前端需要的添加后的完整信息
        return R.success(cart);
    }

    /**
     * 将一份菜品/套餐移出购物车
     * @param shoppingCart 菜品/套餐信息
     * @return 返回移出（减少）结果
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("菜品信息：{}", shoppingCart);

        ShoppingCart cart = shoppingCartService.sub(shoppingCart);

        return R.success(cart);
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
