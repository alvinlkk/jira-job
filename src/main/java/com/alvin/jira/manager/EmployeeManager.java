/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.manager;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;

import com.alvin.jira.pojo.Employee;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 员工管理类
 *
 * @author alvin
 * @version 7.x
 * @since 2023/8/12
 **/
@Slf4j
public class EmployeeManager {

    private static List<Employee> EMPS = Lists.newArrayList();

    private EmployeeManager() {
    }

    /**
     * 获取所有的员工
     *
     * @return 用户列表
     */
    public static List<Employee> getAllEmployees() {
        if(CollUtil.isEmpty(EMPS)) {
            synchronized (EmployeeManager.class) {
                if(CollUtil.isEmpty(EMPS)) {
                    load();
                }
            }
        }
        return EMPS;
    }

    private static void load() {
        InputStream inputstream = EmployeeManager.class.getClassLoader().getResourceAsStream("employee.json");
        String json = IoUtil.readUtf8(inputstream);
        if(StrUtil.isNotBlank(json)) {
            List<Employee> employees = JSONUtil.toList(json, Employee.class);
            EmployeeManager.EMPS = employees;
        }
    }


    /**
     * 获取所有的用户名
     *
     * @return 用户名
     */
    public static List<String> getAllUserNames() {
        return getAllEmployees().stream().map(Employee::getUserName).collect(Collectors.toList());
    }

    /**
     * 根据用户名获取对应的手机号
     *
     * @param userName 用户名
     * @return 手机号
     */
    public static String getMobileByUserName(String userName) {
        Optional<Employee> employeeOp = getAllEmployees().stream().filter(item -> StrUtil.equals(userName, item.getUserName())).findFirst();
        if (employeeOp.isPresent()) {
            return employeeOp.get().getMobile();
        }
        ;
        return null;
    }

    /**
     * 根据用户名获取员工真实姓名
     *
     * @param userName 用户名
     * @return 真实姓名
     */
    public static String getRealNameByUserName(String userName) {
        Optional<Employee> employeeOp = getAllEmployees().stream().filter(item -> StrUtil.equals(userName, item.getUserName())).findFirst();
        if (employeeOp.isPresent()) {
            return employeeOp.get().getRealName();
        }
        ;
        return null;
    }

    /**
     * 根据用户名获取对应的员工信息
     *
     * @param userName 用户名
     * @return 员工信息
     */
    public static Employee getUser(String userName) {
        Optional<Employee> employeeOp = getAllEmployees().stream().filter(item -> StrUtil.equals(userName, item.getUserName())).findFirst();
        if (employeeOp.isPresent()) {
            return employeeOp.get();
        }
        ;
        return null;
    }

    /**
     * 判断当前用户是否是测试人员
     *
     * @param username 用户名
     * @return true-测试人员 false-不是测试人员
     */
    public static boolean isTester(String username) {
        if (StrUtil.isEmpty(username)) {
            return false;
        }
        Employee employee = EmployeeManager.getUser(username);
        if (employee == null) {
            return false;
        }
        return "tester".equals(employee.getUserType());
    }
}
