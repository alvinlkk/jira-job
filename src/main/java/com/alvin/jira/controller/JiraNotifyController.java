package com.alvin.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvin.jira.service.DingTalkNotifyService;

/**
 * <p>jira任务通知</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  17:12
 */
@RestController
@RequestMapping("/jira/notify")
public class JiraNotifyController {

    @Autowired
    private DingTalkNotifyService dingTalkNotifyService;

    /**
     * 通知今日未创建任务人员
     * @return
     */
    @GetMapping("/todayUnCreateTasks")
    public String notifyTodayUncreateTasks() {
        dingTalkNotifyService.notifyTodayUncreateTasks();;
        return "ok";
    }

    /**
     * 通知下周未创建任务成员
     * @return
     */
    @GetMapping("/nextWeekUnCreateTasks")
    public String notifynextWeekUnCreateTasks() {
        dingTalkNotifyService.notifyNextWeekUnCreateTasks();;
        return "ok";
    }

    /**
     * 存在过期未关闭任务的人员
     * @return
     */
    @GetMapping("/expireTasks")
    public String notifyExpireTasks() {
        dingTalkNotifyService.notifyExpireTasks();;
        return "ok";
    }
}
