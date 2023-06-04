package com.alvin.jira.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alvin.jira.dto.EmployeeReportItemDTO;
import com.alvin.jira.enums.UserEnum;

import lombok.SneakyThrows;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  10:02
 */
public interface JiraService {


    List<UserEnum> getTodayUnCreatedTaskUsers();

    List<UserEnum> getNextWeekUnCreatedTaskUsers();

    Map<String, List<String>> getUserExpireIssues();


    @SneakyThrows
    List<EmployeeReportItemDTO> getWeeklyReportJiraIssues();

    /**
     * 获取指定周的工作内容
     * @param date 日期，这一周的任何一天
     * @return
     */
    @SneakyThrows
    List<EmployeeReportItemDTO> getWeekReportjiraIssues(Date date);
}
