package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.bean.Category;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.common.R;
import com.zcx.reggie.dto.DishDto;
import com.zcx.reggie.service.CategoryService;
import com.zcx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        // 前端还需要分类名称，使用DishDto封装需要的数据
        Page<DishDto> dishDtoPageInfo = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝(除了records属性)
        BeanUtils.copyProperties(page, dishDtoPageInfo, "records");

        // 处理records
        List<DishDto> records = pageInfo.getRecords()
                .stream()
                .map(dish -> {
                    DishDto dishDto = new DishDto();

                    BeanUtils.copyProperties(dish, dishDto);

                    Long categoryId = dish.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    dishDto.setCategoryName(category.getName());

                    return dishDto;
                }).collect(Collectors.toList());

        // 将处理过的records添加到要返回的分页对象中
        dishDtoPageInfo.setRecords(records);

        return R.success(dishDtoPageInfo);
    }

}
