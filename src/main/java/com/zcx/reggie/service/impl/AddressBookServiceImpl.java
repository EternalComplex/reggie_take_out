package com.zcx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcx.reggie.bean.AddressBook;
import com.zcx.reggie.bean.Dish;
import com.zcx.reggie.mapper.AddressBookMapper;
import com.zcx.reggie.mapper.DishMapper;
import com.zcx.reggie.service.AddressBookService;
import com.zcx.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
