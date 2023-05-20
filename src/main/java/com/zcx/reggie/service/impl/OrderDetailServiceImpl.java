package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.bean.OrderDetail;
import com.zcx.reggie.mapper.DishMapper;
import com.zcx.reggie.mapper.OrderDetailMapper;
import com.zcx.reggie.service.DishService;
import com.zcx.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
