package com.alvin.jira.manager;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alvin.jira.enums.UserEnum;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  10:06
 */
@Component
@Slf4j
public class JiraManager {

    private static final String DATE_FORMATE = "yyyy-MM-dd";

    public static JiraClient getJiraClient() throws JiraException {
        BasicCredentials creds = new BasicCredentials("cxw", "bangsun@123");
        JiraClient jiraClient = new JiraClient("http://10.100.1.17:8080/", creds);
        return jiraClient;
    }

    public static List<Issue> getAllUserTasks(Date startDate, Date endDate) throws JiraException {
        List<String> allUserNames = UserEnum.getAllUserNames();
        String startDateStr = DateUtil.format(startDate, DATE_FORMATE);
        String endDateStr = DateUtil.format(endDate, DATE_FORMATE);
        String userTasksJql = JqlManager.getUserTasksJql(allUserNames, startDateStr, endDateStr);
        JiraClient jiraClient = getJiraClient();
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
    public static List<Issue> getAllExpireTasks(Date endDate) throws JiraException {
        List<String> allUserNames = UserEnum.getAllUserNames();
        String endDateStr = DateUtil.format(endDate, DATE_FORMATE);
        String userTasksJql = JqlManager.getUserExpireTasksJql(allUserNames, endDateStr);
        JiraClient jiraClient = getJiraClient();
        Issue.SearchResult searchResult = jiraClient.searchIssues(userTasksJql, 100000);
        return searchResult.issues;
    }

    /**
     * 获取用户的缺陷列表
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     * @throws JiraException
     */
    public static List<Issue> getUserBugs(Date startDate, Date endDate) throws JiraException {
        List<String> allUserNames = UserEnum.getAllUserNames();
        String startDateStr = DateUtil.format(startDate, DATE_FORMATE);
        String endDateStr = DateUtil.format(endDate, DATE_FORMATE);
        String userTasksJql = JqlManager.getUserBugsJql(allUserNames, startDateStr, endDateStr);
        JiraClient jiraClient = getJiraClient();
        Issue.SearchResult searchResult = jiraClient.searchIssues(userTasksJql, 100000);
        return searchResult.issues;
    }

    public static void main(String[] args) throws JiraException {
        List<Issue> allExpireTasks = getAllExpireTasks(new Date());
        System.out.println(allExpireTasks);
    }

}
