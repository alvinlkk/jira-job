package com.alvin.jira.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvin.jira.dto.UserIssuesDTO;
import com.alvin.jira.enums.UserEnum;
import com.alvin.jira.manager.JiraManager;
import com.alvin.jira.service.BugsStatisticsService;
import com.alvin.jira.service.DingTalkNotifyService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.sf.json.JSONNull;

/**
 * <p>缺陷相关统计服务 </p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/16  17:57
 */
@Service
@Slf4j
public class BugsStatisticsServiceImpl implements BugsStatisticsService {

    @Autowired
    private DingTalkNotifyService dingTalkNotifyService;


    @SneakyThrows
    @Override
    public void notifyUnlogBugReason() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusDays(today.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue() + 7);
        Date startDate = Date.from(lastMonday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Issue> userBugs = JiraManager.getUserBugs(startDate, endDate);
        List<Issue> doneBugs = userBugs.stream().filter(issue -> StrUtil.equals("已解决", issue.getStatus().getName())
                || StrUtil.equals("关闭", issue.getStatus().getName())).collect(Collectors.toList());
        List<Issue> missReasonIssues = doneBugs.stream().filter(issue -> isEmptyBugReason(issue)).collect(Collectors.toList());
        if (CollUtil.isEmpty(missReasonIssues)) {
            log.warn("all closed issues have reason");
            return;
        }

        List<UserIssuesDTO> userIssuesDtos = UserIssuesDTO.buildUserIssuesFromIssues(missReasonIssues);
        dingTalkNotifyService.notifyByTmpl(userIssuesDtos, "未填写缺陷原因模板.ftl", "缺陷原因未登记通知");
    }

    @Override
    public void notifyCurrentWeekUserBugNum() {
        List<UserIssuesDTO> userbugs = this.listCurrentWeekUserBugs();
        dingTalkNotifyService.notifyByTmpl(userbugs, "本周缺陷数量统计模板.ftl", "本周缺陷数量统计");
    }

    @SneakyThrows
    private List<UserIssuesDTO> listCurrentWeekUserBugs() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        Date startDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Issue> userBugs = JiraManager.getUserBugs(startDate, endDate);
        userBugs = userBugs.stream().filter(issue -> !UserEnum.isTester(issue.getAssignee().getName())).collect(Collectors.toList());
        return UserIssuesDTO.buildUserIssuesFromIssues(userBugs);
    }


    private boolean isEmptyBugReason(Issue issue) {
        return issue.getField("customfield_11503") instanceof JSONNull;
    }

}
