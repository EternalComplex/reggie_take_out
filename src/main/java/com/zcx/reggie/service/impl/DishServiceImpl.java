package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.Category;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.bean.DishFlavor;
import com.zcx.reggie.dto.DishDto;
import com.zcx.reggie.mapper.DishMapper;
import com.zcx.reggie.service.CategoryService;
import com.zcx.reggie.service.DishFlavorService;
import com.zcx.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    /**
     * 新增菜品，根据插入菜品对应的口味数据，同时操作dish表和dish_flavor表
     * @param dishDto 封装的数据对象
     */
    @Override
    @Transactional  // 事务管理
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品的基本信息到dish表
        this.save(dishDto);

        // 获取菜品id
        Long dishId = dishDto.getId();

        // 完善dish_flavor表的所需的菜品id字段
        List<DishFlavor> dishFlavors = dishDto.getFlavors()
                .stream()
                .peek(flavor -> flavor.setDishId(dishId))
                .collect(Collectors.toList());

        // 保存菜品的口味信息到dish_flavor表
        dishFlavorService.saveBatch(dishFlavors);
    }

    /**
     * 菜品信息分页查询，将前端所需的数据封装到DishDto中返回分页构造器
     * @param page 指定页数
     * @param pageSize 每页数据条数
     * @param name 模糊查询
     * @return 返回数据
     */
    @Override
    public Page<DishDto> pageForDishDto(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);

        // 前端还需要分类名称这一数据，封装DishDto类型的分页构造器并返回
        Page<DishDto> dishDtoPageInfo = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        this.page(pageInfo, queryWrapper);

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
                    if (category != null) dishDto.setCategoryName(category.getName());

                    return dishDto;
                }).collect(Collectors.toList());

        // 将处理过的records添加到要返回的分页对象中
        dishDtoPageInfo.setRecords(records);

        return dishDtoPageInfo;
    }

    /**
     * 根据菜品id查询菜品信息和口味信息
     * @param id 菜品id
     * @return 返回查询结果
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();

        // 查询菜品信息
        Dish dish = this.getById(id);

        // 查询口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        // 将查询到的信息拷贝到DishDto对象中
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息和口味信息
     * @param dishDto 封装有菜品信息、口味信息的对象
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // 更新菜品信息
        this.updateById(dishDto);

        // 清空原有的口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        // 添加更新后的口味信息
        // 完善dish_flavor表的所需的菜品id字段
        List<DishFlavor> dishFlavors = dishDto.getFlavors()
                .stream()
                .peek(flavor -> flavor.setDishId(dishDto.getId()))
                .collect(Collectors.toList());

        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 批量起售/停售菜品
     * @param st status字段的值，1表示起售，0表示停售
     * @param ids 菜品id
     */
    @Override
    public void updateStatusByIds(int st, String ids) {
        for (String id: ids.split(",")) {
            Dish dish = this.getById(id);
            dish.setStatus(st);

            this.updateById(dish);
        }
    }
}
