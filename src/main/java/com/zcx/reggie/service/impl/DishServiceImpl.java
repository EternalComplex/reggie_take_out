package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.bean.Employee;
import com.zcx.reggie.mapper.DishMapper;
import com.zcx.reggie.mapper.EmployeeMapper;
import com.zcx.reggie.service.DishService;
import com.zcx.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
