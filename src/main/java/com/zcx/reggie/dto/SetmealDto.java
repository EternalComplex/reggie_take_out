package com.zcx.reggie.dto;

import com.zcx.reggie.bean.Setmeal;
import com.zcx.reggie.bean.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
