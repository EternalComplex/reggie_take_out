package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.common.R;
import com.zcx.reggie.dto.DishDto;
import com.zcx.reggie.service.CategoryService;
import com.zcx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto 根据前端传递数据封装的类
     * @return 返回新增结果
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(String.valueOf(dishDto));

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page 指定页数
     * @param pageSize 每页数据条数
     * @param name 模糊查询
     * @return 返回数据
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {

        Page<DishDto> dishDtoPageInfo = dishService.pageForDishDto(page, pageSize, name);

        return R.success(dishDtoPageInfo);
    }

}
