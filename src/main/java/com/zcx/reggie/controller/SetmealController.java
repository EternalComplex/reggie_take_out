package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.bean.Setmeal;
import com.zcx.reggie.common.R;
import com.zcx.reggie.dto.SetmealDto;
import com.zcx.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDto 套餐信息
     * @return 返回新增结果
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息: {}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 根据id返回套餐信息
     * @param setmealId 套餐id
     * @return 返回套餐及菜品信息
     */
    @GetMapping("/{setmealId}")
    public R<SetmealDto> get(@PathVariable String setmealId) {
        SetmealDto setmealDto = setmealService.getByIdWithSetmealDto(setmealId);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息: {}", setmealDto);
        return R.success("套餐信息修改成功");
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
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(String ids) {
        log.info(ids);

        setmealService.removeByIdWithDish(ids);

        return R.success("套餐信息删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal 查询条件
     * @return 返回查询结果
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        log.info("查询条件：{}", setmeal);

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);

        return R.success(setmealList);
    }
}
