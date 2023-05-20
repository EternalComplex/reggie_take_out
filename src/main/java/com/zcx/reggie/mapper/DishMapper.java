package com.zcx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcx.reggie.bean.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
