package com.alvin.jira.service;

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
}
