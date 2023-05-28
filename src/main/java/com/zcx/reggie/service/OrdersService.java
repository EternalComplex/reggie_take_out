package com.zcx.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.reggie.bean.Orders;
import com.zcx.reggie.dto.OrdersDto;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);

    Page<OrdersDto> pageForOrdersDto(int page, int pageSize, String number, String beginTime, String endTime);
}
