package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.common.R;
import com.zcx.reggie.dto.DishDto;
import com.zcx.reggie.service.CategoryService;
import com.zcx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisTemplate<String, List<DishDto>> redisTemplate;

    /**
     * 新增菜品
     * @param dishDto 封装有菜品信息、口味信息的对象
     * @return 返回新增结果
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info(String.valueOf(dishDto));

        dishService.saveWithFlavor(dishDto);

        // 清理某个分类的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

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

    /**
     * 根据id查询菜品信息和口味信息
     * @param id 菜品id
     * @return 返回查询结果
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto 封装有菜品信息、口味信息的对象
     * @return 返回修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info(String.valueOf(dishDto));

        dishService.updateWithFlavor(dishDto);

        // 清除所有菜品的缓存数据
//        Set<String> keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);

        // 清理某个分类的菜品缓存数据
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 批量起售/停售
     * @param st 1: 起售 0: 停售
     * @param ids 菜品id
     * @return 返回更改结果
     */
    @PostMapping("/status/{st}")
    public R<String> status(@PathVariable int st,String ids) {
        log.info(ids);

        dishService.updateStatusByIds(st, ids);

        return R.success("菜品状态更改成功");
    }

    /**
     * 批量删除菜品信息
     * @param ids 菜品id
     * @return 返回删除结果
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        log.info(ids);

        dishService.removeByIds(Arrays.asList(ids.split(",")));

        return R.success("菜品信息删除成功");
    }


//    /**
//     * 根据条件查询菜品数据
//     * @param dish 查询条件
//     * @return 返回查询数据
//     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper
//                .eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId())
//                .eq(Dish::getStatus, 1)
//                .orderByDesc(Dish::getSort)
//                .orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> dishList = dishService.list(queryWrapper);
//
//        return R.success(dishList);
//    }

    /**
     * 根据条件查询菜品数据(同时包含口味信息)
     * @param dish 查询条件
     * @return 返回查询数据
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        log.info("查询条件：{}", dish);

        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        // 从redis中获取缓存数据
        dishDtoList = redisTemplate.opsForValue().get(key);

        // 若redis中没有缓存数据，则从数据库中查询，并添加到redis缓存中
        if (dishDtoList == null) {
            dishDtoList = dishService.getDishDtoList(dish);
            redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);
        }

        return R.success(dishDtoList);
    }
}
