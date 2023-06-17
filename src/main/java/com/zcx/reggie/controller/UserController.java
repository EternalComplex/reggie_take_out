package com.zcx.reggie.controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.reggie.bean.User;
import com.zcx.reggie.common.R;
import com.zcx.reggie.service.UserService;
import com.zcx.reggie.utils.SMSUtils;
import com.zcx.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送手机短信验证码
     * @param user 接收手机号信息
     * @return 返回发送结果
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession session, @RequestBody User user) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            // 保存一份到session
//            session.setAttribute(phone, code);

            // 将生成的验证码缓存到redis中，并设置5分钟的有效期
            stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);


            log.info("生成的验证码为：{}", code);

            // 调用阿里云短信服务API完成发送
            try {
                SendSmsResponse smsResponse = SMSUtils.sendVerifyCode(phone, code);

                log.info("消息的发送状态：{}", SMSUtils.querySendDetails(phone, smsResponse.getBizId()).getMessage());
                log.info("错误的原因：{}", SMSUtils.getSmsSendError(smsResponse.getCode()));

                return R.success("手机短信验证码发送成功");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return R.error("手机短信验证码发送失败");
    }

    /**
     * 移动端用户登录
     * @param map 接收手机号与验证码
     * @return 返回登录结果
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {
        log.info(map.toString());

        // 获取手机号和验证码
        String phone = map.get("phone");
        String code = map.get("code");

        // 比对session中保存的验证码
//        String codeInSession = session.getAttribute(phone);

        // 从redis中获取缓存的验证码
        String codeInRedis = stringRedisTemplate.opsForValue().get(phone);

        // 验证码比对
        if (code.equals(codeInRedis)) {
            // 判断当前手机号是否为新用户，若是新用户则自动完成注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(userLambdaQueryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);

                userService.save(user);
            }

            // 向session中添加用户id以通过过滤器
            session.setAttribute("user", user.getId());

            // 用户登录成功，删除redis中缓存的验证码
            stringRedisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登陆失败");
    }
}
