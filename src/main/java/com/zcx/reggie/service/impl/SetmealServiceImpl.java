package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.Category;
import com.zcx.reggie.bean.Setmeal;
import com.zcx.reggie.bean.SetmealDish;
import com.zcx.reggie.common.CustomException;
import com.zcx.reggie.dto.SetmealDto;
import com.zcx.reggie.mapper.SetmealMapper;
import com.zcx.reggie.service.CategoryService;
import com.zcx.reggie.service.SetmealDishService;
import com.zcx.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private CategoryService categoryService;

    /**
     * 新增套餐，同时保存套餐与菜品的关联关系
     * @param setmealDto 套餐与菜品信息
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息，操作setmeal表
        this.save(setmealDto);

        // 保存套餐与菜品的关联信息，操作setmeal_dish表
        List<SetmealDish> collect = setmealDto.getSetmealDishes().stream()
                .peek(item -> item.setSetmealId(setmealDto.getId()))
                .collect(Collectors.toList());

        setmealDishService.saveBatch(collect);
    }

    /**
     * 套餐信息分页查询
     * @param page 指定页数
     * @param pageSize 每页数据条数
     * @param name 模糊查询
     * @return 返回数据
     */
    @Override
    public Page<SetmealDto> pageWithSetmealDto(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> setmealDtoPageInfo = new Page<>();

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper
                .like(name != null, Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);

        this.page(pageInfo, setmealLambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo, setmealDtoPageInfo, "records");

        List<SetmealDto> collect = pageInfo.getRecords().stream()
                .map(item -> {
                    SetmealDto setmealDto = new SetmealDto();

                    BeanUtils.copyProperties(item, setmealDto);

                    Long categoryId = item.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    setmealDto.setCategoryName(category.getName());

                    return setmealDto;
                })
                .collect(Collectors.toList());

        setmealDtoPageInfo.setRecords(collect);

        return setmealDtoPageInfo;
    }

    /**
     * 批量起售/停售
     * @param st 1: 起售 0: 停售
     * @param ids 套餐id
     * @return 返回更改结果
     */
    @Override
    public void updateStatusByIds(int st, String ids) {
        for (String id: ids.split(",")) {
            Setmeal setmeal = this.getById(id);
            setmeal.setStatus(st);

            this.updateById(setmeal);
        }
    }

    /**
     * 批量删除套餐信息
     * @param ids 套餐id
     * @return 返回删除结果
     */
    @Override
    @Transactional
    public void removeByIdWithDish(String ids) {
        for (String id: ids.split(",")) {
            // 查询套餐状态，若在售则不可删除
            Setmeal setmeal = this.getById(id);
            if (setmeal.getStatus() == 1) throw new CustomException("套餐正在售卖中，不能删除");

            // 删除setmeal表的信息
            this.removeById(id);

            // 删除setmeal_dish表的信息
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);

            setmealDishService.remove(setmealDishLambdaQueryWrapper);
        }
    }
}
