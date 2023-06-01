package com.alvin.jira.service.impl;

import java.util.ArrayList;;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;

import com.alvin.jira.enums.UserEnum;
import com.alvin.jira.manager.JiraManager;
import com.alvin.jira.service.JiraService;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraException;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  10:03
 */
@Service
@Slf4j
public class JiraServiceImpl implements JiraService {

    @SneakyThrows
    @Override
    public List<UserEnum> getTodayUnCreatedTaskUsers() {
        return getUnCreateTaskUsersBetweenDueDate(new Date(), new Date());
    }

    @SneakyThrows
    @Override
    public List<UserEnum> getNextWeekUnCreatedTaskUsers() {
        DateTime now = DateTime.now();
        DateTime dateTime = now.withDayOfWeek(DateTimeConstants.MONDAY);
        DateTime nextWeekMonday = dateTime.plusWeeks(1);
        String dueDateStart = nextWeekMonday.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
        System.out.println(dueDateStart);
        DateTime nextWeekFriday = dateTime.plusDays(5);
        String dueDateEnd = nextWeekFriday.toString(DateTimeFormat.forPattern("yyyy-MM-dd"));
        System.out.println(dueDateEnd);
        return getUnCreateTaskUsersBetweenDueDate(nextWeekMonday.toDate(), nextWeekFriday.toDate());
    }

    private List<UserEnum> getUnCreateTaskUsersBetweenDueDate(Date nextWeekMonday, Date nextWeekFriday) throws JiraException {
        List<Issue> allUserTasks = JiraManager.getAllUserTasks(nextWeekMonday, nextWeekFriday);
        Map<String, Integer> userTimeTotalMap = allUserTasks.stream()
                .collect(Collectors.groupingBy(issue -> issue.getAssignee().getName(),
                        Collectors.summingInt(item -> {
                            Object hours = item.getField("customfield_12100");
                            if (hours == null || StrUtil.isBlank(hours.toString())) {
                                return 0;
                            }
                            return Double.valueOf(hours.toString()).intValue();
                        })));

        List<UserEnum> uncreateTaskUsers = new ArrayList<>();
        for (UserEnum userEnum : UserEnum.values()) {
            Integer totalTime = userTimeTotalMap.getOrDefault(userEnum.getUserName(), 0);
            if (totalTime < 8) {
                uncreateTaskUsers.add(userEnum);
            }
        }
        return uncreateTaskUsers;
    }


    @Override
    public Map<String, List<String>> getUserExpireIssues() {
        List<Issue> allExpireIssues = null;
        try {
            allExpireIssues = JiraManager.getAllExpireTasks(new Date());
        } catch (JiraException e) {
            e.printStackTrace();
        }
        Map<String, List<String>> userJiraIdsMap = allExpireIssues.stream()
                .collect(Collectors.groupingBy(issue -> issue.getAssignee().getName(),
                        Collectors.mapping(Issue::getKey, Collectors.toList())));
        return userJiraIdsMap;
    }
}
