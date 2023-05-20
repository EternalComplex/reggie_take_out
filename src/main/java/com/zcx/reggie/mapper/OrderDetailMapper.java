package com.zcx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.bean.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
