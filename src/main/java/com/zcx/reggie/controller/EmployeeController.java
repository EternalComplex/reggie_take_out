package com.zcx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcx.reggie.bean.Employee;
import com.zcx.reggie.common.R;
import com.zcx.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 员工登录、管理
 */
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

        // 以下代码使用 公共字段自动填充 的方法实现
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page 指定页数
     * @param pageSize 每页数据条数
     * @param name 模糊查询
     * @return 返回数据
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        // 分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询，查询到的数据存放在pageInfo的records属性中
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据员工id修改信息
     * @param employee 员工信息
     * @return 返回修改结果
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        // 若某一字段为null，MP会保留原有值
        // 由于雪花算法生成的Long类型的18位id会导致前端js精度的缺失（16位），所以通过在配置类WebMvcConfig中扩展消息转换器，将id转为String传到前端，以此解决传来的id精度缺失

        // 以下代码使用 公共字段自动填充 的方法实现
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id 员工id
     * @return 返回员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        if (emp != null) {
            return R.success(emp);
        }
        return R.error("没有查询到对应员工信息");
    }
}
