package com.alvin.jira.service;

import java.util.List;

import com.alvin.jira.dto.UserIssuesDTO;

import lombok.SneakyThrows;

/**
 * <p>描 述：</p>
 * 员工任务统计服务
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  16:59
 */
public interface TaskStatisticsService {

    /**
     * 通知当天未创建任务员工
     */
    void notifyTodayUncreateTasks();

    /**
     * 通知下周未创建任务员工
     */
    void notifyNextWeekUnCreateTasks();

    /**
     * 过期任务通知
     */
    void notifyExpireTasks();
}
