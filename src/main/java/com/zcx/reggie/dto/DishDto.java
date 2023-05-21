package com.zcx.reggie.dto;

import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.bean.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
