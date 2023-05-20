package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.bean.Category;
import com.zcx.reggie.common.R;
import com.zcx.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category 前端传来的分类数据
     * @return 返回新增结果
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("Category：{}", category);

        categoryService.save(category);

        return R.success("新增分类成功");
    }

    /**
     * 分类信息分页查询
     * @param page 指定页数
     * @param pageSize 每页数据条数
     * @return 返回数据
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }
}
