package com.zcx.reggie.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.bean.Orders;
import com.zcx.reggie.common.R;
import com.zcx.reggie.dto.OrdersDto;
import com.zcx.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

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

    /**
     * 用户端分页查询订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        Page<OrdersDto> ordersDtoPageInfo = ordersService.pageForOrdersDto(page, pageSize, null, null, null);

        return R.success(ordersDtoPageInfo);
    }

    /**
     * 管理端分页查询订单信息
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<OrdersDto>> page(int page, int pageSize, String number, String beginTime, String endTime) {
        log.info("page：{}，pageSize：{}，number：{}，beginTime：{}，endTIme：{}", page, pageSize, number, beginTime, endTime);

        Page<OrdersDto> ordersDtoPageInfo = ordersService.pageForOrdersDto(page, pageSize, number, beginTime, endTime);

        return R.success(ordersDtoPageInfo);
    }

    /**
     * 修改订单状态
     * @param map
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Map<String, String> map) {
        String id = map.get("id");
        Integer status = Integer.parseInt(map.get("status"));

        log.info("id：{}，status：{}", id, status);

        Orders orders = ordersService.getById(id);
        orders.setStatus(status);

        ordersService.updateById(orders);

        return R.success("订单状态修改成功");
    }
}
