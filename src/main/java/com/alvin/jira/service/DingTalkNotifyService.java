package com.alvin.jira.service;

import java.util.List;

import com.alvin.jira.dto.UserIssuesDTO;

import lombok.SneakyThrows;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  16:59
 */
public interface DingTalkNotifyService {

    void notifyTodayUncreateTasks();

    void notifyNextWeekUnCreateTasks();

    void notifyExpireTasks();

    /**
     * 根据模板通知
     *
     * @param users 通知的用户
     * @param tmplName 模板名称
     * @param notifyTitle 通知标题
     */
    void notifyByTmpl(List<UserIssuesDTO> users, String tmplName, String notifyTitle);
}
