package com.alvin.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvin.jira.service.DingTalkNotifyService;

/**
 * <p>描 述：</p>
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

    @GetMapping("/todayUnCreateTasks")
    public String notifyTodayUncreateTasks() {
        dingTalkNotifyService.notifyTodayUncreateTasks();;
        return "ok";
    }

    @GetMapping("/nextWeekUnCreateTasks")
    public String notifynextWeekUnCreateTasks() {
        dingTalkNotifyService.notifyNextWeekUnCreateTasks();;
        return "ok";
    }


    @GetMapping("/expireTasks")
    public String notifyExpireTasks() {
        dingTalkNotifyService.notifyExpireTasks();;
        return "ok";
    }
}
