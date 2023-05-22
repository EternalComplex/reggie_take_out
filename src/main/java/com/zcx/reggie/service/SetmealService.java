package com.zcx.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.reggie.bean.Setmeal;
import com.zcx.reggie.dto.SetmealDto;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto setmealDto);

    public Page<SetmealDto> pageWithSetmealDto(int page, int pageSize, String name);

    public void updateStatusByIds(int st, String ids);

    void removeByIdWithDish(String ids);
}
