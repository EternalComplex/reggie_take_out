package com.zcx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcx.reggie.bean.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
