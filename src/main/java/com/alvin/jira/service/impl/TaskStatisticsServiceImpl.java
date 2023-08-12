package com.alvin.jira.service.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvin.jira.dto.UserIssuesDTO;
import com.alvin.jira.manager.DingTalkNotifyManager;
import com.alvin.jira.manager.EmployeeManager;
import com.alvin.jira.manager.JiraManager;
import com.alvin.jira.pojo.Employee;
import com.alvin.jira.service.TaskStatisticsService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;

/**
 * 任务统计服务实现类
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  16:59
 */
@Service
@Slf4j
public class TaskStatisticsServiceImpl implements TaskStatisticsService {

    @Autowired
    private JiraManager jiraManager;

    @Autowired
    private DingTalkNotifyManager dingTalkNotifyManager;

    @Override
    public void notifyTodayUncreateTasks() {
        List<Employee> unCreateTaskUsersBetweenDueDate = jiraManager.getUnCreateTaskUsersBetweenDueDate(new Date(), new Date());
        if (CollUtil.isEmpty(unCreateTaskUsersBetweenDueDate)) {
            return;
        }

        notifyUnCreateTaskUsers("当日Jira任务未创建.txt", unCreateTaskUsersBetweenDueDate);
    }

    @Override
    public void notifyNextWeekUnCreateTasks() {
        DateTime now = DateTime.now();
        DateTime dateTime = now.withDayOfWeek(DateTimeConstants.MONDAY);
        DateTime nextWeekMonday = dateTime.plusWeeks(1);
        DateTime nextWeekFriday = nextWeekMonday.plusDays(5);
        List<Employee> unCreateTaskUsers = jiraManager.getUnCreateTaskUsersBetweenDueDate(nextWeekMonday.toDate(), nextWeekFriday.toDate());
        if (CollUtil.isEmpty(unCreateTaskUsers)) {
            return;
        }
        notifyUnCreateTaskUsers("下周任务未创建.txt", unCreateTaskUsers);
    }

    private void notifyUnCreateTaskUsers(String name, List<Employee> unCreatedTaskUsers) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(name);
        String textTmpl = IoUtil.readUtf8(inputStream);
        List<String> mobiles = unCreatedTaskUsers.stream().map(Employee::getMobile).collect(Collectors.toList());
        String atMobiles = mobiles.stream().map(item -> "@" + item).collect(Collectors.joining(" "));
        Map<String, String> params = new HashMap<>();
        params.put("atMobiles", atMobiles);
        String text = StrUtil.format(textTmpl, params);
        dingTalkNotifyManager.sendText(text, mobiles);
    }

    @SneakyThrows
    @Override
    public void notifyExpireTasks() {
        List<Issue> allExpireIssues = jiraManager.getAllExpireTasks(new Date());
        ;
        Map<String, List<String>> userExpireIssues = allExpireIssues.stream()
                .collect(Collectors.groupingBy(issue -> issue.getAssignee().getName(),
                        Collectors.mapping(Issue::getKey, Collectors.toList())));
        if (CollUtil.isEmpty(userExpireIssues)) {
            return;
        }

        List<UserIssuesDTO> userExpireIssuesDtos = new ArrayList<>();
        userExpireIssues.forEach((username, jiraIds) -> {
            UserIssuesDTO userExpireIssuesDto = new UserIssuesDTO();
            String mobile = EmployeeManager.getMobileByUserName(username);
            userExpireIssuesDto.setMobile(mobile);
            userExpireIssuesDto.setJiraIds(jiraIds);
            userExpireIssuesDtos.add(userExpireIssuesDto);
        });
        TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setTemplateLoader(templateLoader);
        ;
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Map<String, Object> params = new HashMap<>();
        params.put("users", userExpireIssuesDtos);
        Template temp = cfg.getTemplate("存在过期任务模板.ftl");
        StringWriter stringWriter = new StringWriter();
        temp.process(params, stringWriter);

        List<String> mobiles = userExpireIssuesDtos.stream().map(UserIssuesDTO::getMobile).collect(Collectors.toList());
        dingTalkNotifyManager.sendMarkDown("过期任务", stringWriter.toString(), mobiles);
    }


}
