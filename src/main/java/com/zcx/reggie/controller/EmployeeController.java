package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcx.reggie.bean.Employee;
import com.zcx.reggie.common.R;
import com.zcx.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request 用于登录成功后将员工id存到session
     * @param employee 接收前端传递过来的数据
     * @return 返回登录结果
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 对密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 根据用户名查询用户信息
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 判断是否存在该用户
        if (emp == null) {
            return R.error("用户名错误");
        }

        // 密码比对
        if (emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        // 查看员工状态
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        // 到达此处说明登录成功，将员工id存到session并返回登录结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 退出登录
     * @param request 用于删除session中的用户信息
     * @return 返回退出结果
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清除session中保存的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request 用于获取session的用户信息
     * @param employee 前端传来的员工信息
     * @return 返回结果
     */
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        // 补充空缺字段
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }
}
