package com.alvin.jira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alvin.jira.service.BugsStatisticsService;

import lombok.SneakyThrows;

/**
 * <p>团队bug统计：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/16  17:46
 */
@RestController
@RequestMapping("/bugs/statistics")
public class BugsStatisticsController {

    @Autowired
    private BugsStatisticsService bugReportService;

    @GetMapping("/notifyCurrentWeekUserBugNum")
    public String notifyCurrentWeekUserBugNum() {
        bugReportService.notifyCurrentWeekUserBugNum();
        return "success";
    }

}
