package com.zcx.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.dto.DishDto;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public Page<DishDto> pageForDishDto(int page, int pageSize, String name);

    public DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
