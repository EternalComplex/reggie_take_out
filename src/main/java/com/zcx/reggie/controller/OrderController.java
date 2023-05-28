package com.zcx.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.bean.Orders;
import com.zcx.reggie.common.R;
import com.zcx.reggie.dto.OrdersDto;
import com.zcx.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders 订单信息
     * @return 返回下单结果
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);

        ordersService.submit(orders);

        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        Page<OrdersDto> ordersDtoPageInfo = ordersService.pageForOrdersDto(page, pageSize);

        return R.success(ordersDtoPageInfo);
    }
}
