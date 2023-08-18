package com.alvin.jira.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alvin.jira.pojo.Employee;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

/**
 * <p>描 述：</p>
 * Jira通用管理类
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  10:06
 */
@Component
@Slf4j

public class JiraManager {

    private static final String DATE_FORMATE = "yyyy-MM-dd";

    @Autowired
    private JiraClient jiraClient;

    /**
     * 获取用户任务的查询语句
     *
     * @param users
     * @param dueDateStart
     * @param dueDateEnd
     * @return
     */
    public static String getUserTasksJql(List<String> users, String dueDateStart, String dueDateEnd) {
        String jqlTmpl = "project in (SDM, ZNJCPTYH, FEATURE,PD) AND issuetype in (子任务-产品, \"子任务-开发(前端)\", \"子任务-开发(后端)\", 子任务-测试, 子任务-设计, \"需求(敏捷)\")  AND due >= {} AND due <= {} AND assignee in ({}) ORDER BY priority DESC";
        return StrUtil.format(jqlTmpl, dueDateStart, dueDateEnd, CollUtil.join(users, ","));
    }

    /**
     * 获取用户过期的查询语句
     *
     * @param users
     * @param dueDateStr
     * @return
     */
    public static String getUserExpireTasksJql(List<String> users, String dueDateStr) {
        String jqlTmpl = "resolution = Unresolved and due <= {} AND assignee in ({}) ORDER BY priority DESC";
        return StrUtil.format(jqlTmpl, dueDateStr, CollUtil.join(users, ","));
    }

    /**
     * 获取指定时间范围内用户的缺陷和改进列表
     *
     * @param users     用户
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     */
    public static String getUserBugsJql(List<String> users, String startDate, String endDate) {
        String jqlTmpl = "issuetype in (Bug, 缺陷, 问题) AND created >= {} AND created <= {} AND assignee in ({})";
        return StrUtil.format(jqlTmpl, startDate, endDate, CollUtil.join(users, ","));
    }

    /**
     * 获取指定时间范围内用户全部的jira任务
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws JiraException
     */
    public List<Issue> getAllUserTasks(Date startDate, Date endDate) throws JiraException {
        List<String> allUserNames = EmployeeManager.getAllUserNames();
        String startDateStr = DateUtil.format(startDate, DATE_FORMATE);
        String endDateStr = DateUtil.format(endDate, DATE_FORMATE);
        String userTasksJql = getUserTasksJql(allUserNames, startDateStr, endDateStr);
        Issue.SearchResult searchResult = jiraClient.searchIssues(userTasksJql, 100000);
        return searchResult.issues;
    }

    /**
     * 获取所有过期的任务
     *
     * @param endDate 截止时间
     * @return
     * @throws JiraException
     */
    public List<Issue> getAllExpireTasks(Date endDate) throws JiraException {
        List<String> allUserNames = EmployeeManager.getAllUserNames();
        String endDateStr = DateUtil.format(endDate, DATE_FORMATE);
        String userTasksJql = getUserExpireTasksJql(allUserNames, endDateStr);
        Issue.SearchResult searchResult = jiraClient.searchIssues(userTasksJql, 100000);
        return searchResult.issues;
    }

    /**
     * 获取用户的缺陷列表
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return
     * @throws JiraException
     */
    public List<Issue> getUserBugs(Date startDate, Date endDate) throws JiraException {
        List<String> allUserNames = EmployeeManager.getAllUserNames();
        String startDateStr = DateUtil.format(startDate, DATE_FORMATE);
        String endDateStr = DateUtil.format(endDate, DATE_FORMATE);
        String userTasksJql = getUserBugsJql(allUserNames, startDateStr, endDateStr);
        Issue.SearchResult searchResult = jiraClient.searchIssues(userTasksJql, 100000);
        return searchResult.issues;
    }


    /**
     * 获取未创建任务用户
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @SneakyThrows
    public List<Employee> getUnCreateTaskUsersBetweenDueDate(Date startDate, Date endDate) {
        List<Issue> allUserTasks = this.getAllUserTasks(startDate, endDate);
        Map<String, Integer> userTimeTotalMap = allUserTasks.stream()
                .collect(Collectors.groupingBy(issue -> issue.getAssignee().getName(),
                        Collectors.summingInt(item -> {
                            Object hours = item.getField("customfield_12100");
                            if (hours == null || StrUtil.isBlank(hours.toString())) {
                                return 0;
                            }
                            if (StrUtil.equals("null", hours.toString())) {
                                return 0;
                            }
                            return Double.valueOf(hours.toString()).intValue();
                        })));

        List<Employee> uncreateTaskUsers = new ArrayList<>();
        for (Employee employee : EmployeeManager.getAllEmployees()) {
            Integer totalTime = userTimeTotalMap.getOrDefault(employee.getUserName(), 0);
            if (totalTime < 8) {
                uncreateTaskUsers.add(employee);
            }
        }
        return uncreateTaskUsers;
    }
}
