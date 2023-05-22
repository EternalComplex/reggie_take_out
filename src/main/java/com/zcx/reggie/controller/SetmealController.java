package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.common.R;
import com.zcx.reggie.dto.SetmealDto;
import com.zcx.reggie.service.SetmealDishService;
import com.zcx.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     * @param setmealDto 套餐信息
     * @return 返回新增结果
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐信息分页查询
     * @param page 指定页数
     * @param pageSize 每页数据条数
     * @param name 模糊查询
     * @return 返回数据
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        Page<SetmealDto> pageInfo = setmealService.pageWithSetmealDto(page, pageSize, name);
        return R.success(pageInfo);
    }

    /**
     * 批量起售/停售
     * @param st 1: 起售 0: 停售
     * @param ids 套餐id
     * @return 返回更改结果
     */
    @PostMapping("/status/{st}")
    public R<String> status(@PathVariable int st,String ids) {
        log.info(ids);

        setmealService.updateStatusByIds(st, ids);

        return R.success("套餐状态更改成功");
    }

    /**
     * 批量删除套餐信息
     * @param ids 套餐id
     * @return 返回删除结果
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        log.info(ids);

        setmealService.removeByIdWithDish(ids);

        return R.success("套餐信息删除成功");
    }
}
