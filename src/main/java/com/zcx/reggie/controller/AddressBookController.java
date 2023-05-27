package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zcx.reggie.bean.AddressBook;
import com.zcx.reggie.common.BaseContext;
import com.zcx.reggie.common.R;
import com.zcx.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Resource
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook 地址信息
     * @return 返回添加结果
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        log.info("addressBook：{}", addressBook);

        // 设置用户id
        addressBook.setUserId(BaseContext.getCurrentId());

        // 若当前用户没有地址，则该地址设置为默认地址
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, addressBook.getUserId());
        if (addressBookService.list(queryWrapper).size() == 0) addressBook.setIsDefault(1);

        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    /**
     * 修改地址
     * @param addressBook 地址信息
     * @return 返回修改结果
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook) {
        addressBookService.updateById(addressBook);
        return R.success("地址信息修改成功");
    }

    /**
     * 删除地址
     * @param ids 地址id
     * @return 返回删除结果
     */
    @DeleteMapping
    public R<String> delete(String ids) {
        log.info("待删除地址的用户id: {}", ids);

        addressBookService.removeById(ids);

        return R.success("地址信息删除成功");
    }

    /**
     * 设置默认地址
     * @param addressBook 地址信息
     * @return 返回设置结果
     */
    @PutMapping("/default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook：{}", addressBook);

        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        // 先将所有地址设置为0
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId()).set(AddressBook::getIsDefault, 0);
        addressBookService.update(updateWrapper);

        // 将当前地址设置为默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return R.success(addressBook);
    }

    /**
     * 查询默认地址
     * @return 返回地址信息
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId()).eq(AddressBook::getIsDefault, 1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (addressBook == null) return R.error("没有找到默认地址");
        return R.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook == null) return R.error("没有找到该对象");
        return R.success(addressBook);
    }

    /**
     * 查询指定用户的地址信息
     * @param addressBook 地址信息
     * @return 返回查询结果
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId())
                .orderByDesc(AddressBook::getUpdateTime);

        return R.success(addressBookService.list(queryWrapper));
    }
}
