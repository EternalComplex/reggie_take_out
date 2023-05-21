package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.bean.DishFlavor;
import com.zcx.reggie.dto.DishDto;
import com.zcx.reggie.mapper.DishMapper;
import com.zcx.reggie.service.DishFlavorService;
import com.zcx.reggie.service.DishService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，根据插入菜品对应的口味数据，同时操作dish表和dish_flavor表
     * @param dishDto 封装的数据对象
     */
    @Override
    @Transactional  // 事务管理
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到dish表
        this.save(dishDto);

        // 获取菜品id
        Long dishId = dishDto.getId();

        // 完善dish_flavor表的所需的菜品id字段
        List<DishFlavor> dishFlavors = dishDto.getFlavors()
                .stream()
                .peek(flavor -> flavor.setDishId(dishId))
                .collect(Collectors.toList());

        // 保存菜品的口味信息到dish_flavor表
        dishFlavorService.saveBatch(dishFlavors);
    }
}
