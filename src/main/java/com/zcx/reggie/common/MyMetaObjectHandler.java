package com.zcx.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作时自动填充
     * @param metaObject 反射工具类
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");

        if (metaObject.hasSetter("createTime")) metaObject.setValue("createTime", LocalDateTime.now());
        if (metaObject.hasSetter("updateTime")) metaObject.setValue("updateTime", LocalDateTime.now());
        if (metaObject.hasSetter("createUser")) metaObject.setValue("createUser", BaseContext.getCurrentId());
        if (metaObject.hasSetter("updateUser")) metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作时，自动填充
     * @param metaObject 反射工具类
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");

        if (metaObject.hasSetter("updateTime")) metaObject.setValue("updateTime", LocalDateTime.now());
        if (metaObject.hasSetter("updateUser")) metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
