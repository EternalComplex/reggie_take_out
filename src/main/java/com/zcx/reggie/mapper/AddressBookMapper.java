package com.zcx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcx.reggie.bean.AddressBook;
import com.zcx.reggie.bean.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
