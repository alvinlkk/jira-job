package com.alvin.jira.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alvin.jira.service.BugsStatisticsService;
import com.alvin.jira.service.TaskStatisticsService;

import lombok.SneakyThrows;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  19:21
 */
@Component
public class JiraNotifyScheduleJob {

    @Autowired
    private TaskStatisticsService dingTalkNotifyService;

    @Autowired
    private BugsStatisticsService bugsStatisticsService;

    @Scheduled(cron = "0 0 10,11 ? * 1-5")
    public void notifyTodayUncreateTasks() {
        dingTalkNotifyService.notifyTodayUncreateTasks();
    }

    @Scheduled(cron = "0 0 14-18 ? * 5")
    public void notifynextWeekUnCreateTasks() {
        dingTalkNotifyService.notifyNextWeekUnCreateTasks();
    }
    
    @Scheduled(cron = "0 0/5 18 ? * 1-5")
    public void notifyExpireTasks() {
        dingTalkNotifyService.notifyExpireTasks();
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 19 ? * 1-5")
    public void notifyUserBugNum() {
        bugsStatisticsService.notifyCurrentWeekUserBugNum();
    }

}
