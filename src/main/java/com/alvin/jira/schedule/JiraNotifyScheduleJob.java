package com.alvin.jira.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import com.alvin.jira.service.DingTalkNotifyService;

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
    private DingTalkNotifyService dingTalkNotifyService;

    @Scheduled(cron = "0 0 10,11 ? * 1-5")
    public void notifyTodayUncreateTasks() {
        dingTalkNotifyService.notifyTodayUncreateTasks();
    }

    @Scheduled(cron = "0 0 14-18 ? * 5")
    public void notifynextWeekUnCreateTasks() {
        dingTalkNotifyService.notifyNextWeekUnCreateTasks();
    }

    @Scheduled(cron = "0 0 11,12 ? * 1-5")
    public void notifyExpireTasks() {
        dingTalkNotifyService.notifyExpireTasks();
    }

}