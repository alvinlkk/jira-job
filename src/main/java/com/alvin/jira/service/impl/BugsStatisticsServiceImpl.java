package com.alvin.jira.service.impl;

import java.io.StringWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvin.jira.dto.UserIssuesDTO;
import com.alvin.jira.manager.DingTalkNotifyManager;
import com.alvin.jira.manager.EmployeeManager;
import com.alvin.jira.manager.JiraManager;
import com.alvin.jira.service.BugsStatisticsService;
import com.alvin.jira.service.TaskStatisticsService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
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
    private JiraManager jiraManager;

    @Autowired
    private DingTalkNotifyManager dingTalkNotifyManager;


    @SneakyThrows
    @Override
    public void notifyUnlogBugReason() {
        LocalDate today = LocalDate.now();
        LocalDate lastMonday = today.minusDays(today.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue() + 7);
        Date startDate = Date.from(lastMonday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Issue> userBugs = jiraManager.getUserBugs(startDate, endDate);
        List<Issue> doneBugs = userBugs.stream().filter(issue -> StrUtil.equals("已解决", issue.getStatus().getName())
                || StrUtil.equals("关闭", issue.getStatus().getName())).collect(Collectors.toList());
        List<Issue> missReasonIssues = doneBugs.stream().filter(issue -> isEmptyBugReason(issue)).collect(Collectors.toList());
        if (CollUtil.isEmpty(missReasonIssues)) {
            log.warn("all closed issues have reason");
            return;
        }

        List<UserIssuesDTO> userIssuesDtos = UserIssuesDTO.buildUserIssuesFromIssues(missReasonIssues);
        this.notifyByTmpl(userIssuesDtos, "未填写缺陷原因模板.ftl", "缺陷原因未登记通知");
    }

    @Override
    public void notifyCurrentWeekUserBugNum() {
        List<UserIssuesDTO> userbugs = this.listCurrentWeekUserBugs();
        this.notifyByTmpl(userbugs, "本周缺陷数量统计模板.ftl", "本周缺陷数量统计");
    }

    @SneakyThrows
    private List<UserIssuesDTO> listCurrentWeekUserBugs() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        Date startDate = Date.from(monday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Issue> userBugs = jiraManager.getUserBugs(startDate, endDate);
        userBugs = userBugs.stream().filter(issue -> !EmployeeManager.isTester(issue.getAssignee().getName())).collect(Collectors.toList());
        return UserIssuesDTO.buildUserIssuesFromIssues(userBugs);
    }


    private boolean isEmptyBugReason(Issue issue) {
        return issue.getField("customfield_11503") instanceof JSONNull;
    }

    @SneakyThrows
    private void notifyByTmpl(List<UserIssuesDTO> users, String tmplName, String notifyTitle) {
        if (CollUtil.isEmpty(users)) {
            log.warn("没有用户需要通知");
        }

        TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setTemplateLoader(templateLoader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Map<String, Object> params = new HashMap<>();
        params.put("users", users);
        Template temp = cfg.getTemplate(tmplName);
        StringWriter stringWriter = new StringWriter();
        temp.process(params, stringWriter);
        List<String> mobiles = users.stream().map(UserIssuesDTO::getMobile).collect(Collectors.toList());
        dingTalkNotifyManager.sendMarkDown(notifyTitle, stringWriter.toString(), mobiles);
    }
}
