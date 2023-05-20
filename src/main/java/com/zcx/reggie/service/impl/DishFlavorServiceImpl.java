package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.bean.DishFlavor;
import com.zcx.reggie.mapper.DishFlavorMapper;
import com.zcx.reggie.mapper.DishMapper;
import com.zcx.reggie.service.DishFlavorService;
import com.zcx.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
