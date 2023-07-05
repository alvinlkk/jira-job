package com.alvin.jira.service.impl;

import java.util.ArrayList;;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.stereotype.Service;

import com.alvin.jira.dto.EmployeeReportItemDTO;
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
        DateTime nextWeekFriday = nextWeekMonday.plusDays(5);
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
                            if(StrUtil.equals("null", hours.toString())) {
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


    @SneakyThrows
    @Override
    public Map<String, List<String>> getUserExpireIssues() {
        List<Issue> allExpireIssues = JiraManager.getAllExpireTasks(new Date());;
        Map<String, List<String>> userJiraIdsMap = allExpireIssues.stream()
                .collect(Collectors.groupingBy(issue -> issue.getAssignee().getName(),
                        Collectors.mapping(Issue::getKey, Collectors.toList())));
        return userJiraIdsMap;
    }


    @Override
    @SneakyThrows
    public List<EmployeeReportItemDTO> getWeeklyReportJiraIssues() {
        DateTime now = DateTime.now();
        DateTime dateTime = now.withDayOfWeek(DateTimeConstants.MONDAY);
        DateTime nextWeekMonday = dateTime.plusWeeks(1);
        DateTime nextWeekFriday = nextWeekMonday.plusDays(5);
        List<EmployeeReportItemDTO> reportItems = getDateEmployeeReportItems(dateTime.toDate(), nextWeekFriday.toDate());
        return reportItems;

    }

    private List<EmployeeReportItemDTO> getDateEmployeeReportItems(Date startDate, Date endDate) throws JiraException {
        List<Issue> jiraIssues = JiraManager.getAllUserTasks(startDate, endDate);
        List<EmployeeReportItemDTO> reportItems = new ArrayList<>();
        Map<String, List<Issue>> jiraIssueGroupMap = jiraIssues.stream().collect(Collectors.groupingBy(item -> item.getAssignee().getName()));
        jiraIssueGroupMap.forEach((jiraName, issues) -> {
            UserEnum user = UserEnum.getUser(jiraName);
            if(user == null) {
                return;
            }

            Map<String, Integer> summaryHoursMap = issues.stream().collect(Collectors.groupingBy(issue -> {
                String summary = "";
                if(issue.getParent() != null) {
                    summary += issue.getParent().getSummary() + "——";
                }
                summary += issue.getSummary();
                return summary;
            }, Collectors.summingInt(issue -> {
                Object hours = issue.getField("customfield_12100");
                if (hours == null || StrUtil.isBlank(hours.toString())) {
                    return 0;
                }
                if(StrUtil.equals("null", hours.toString())) {
                    return 0;
                }
                return Double.valueOf(hours.toString()).intValue();
            })));

            summaryHoursMap.forEach((summary, hours) -> {
                EmployeeReportItemDTO employeeReportItem = new EmployeeReportItemDTO();
                employeeReportItem.setName(user.getRealName());
                employeeReportItem.setGroup(user.getUserType());
                employeeReportItem.setCate(user.getUserType());
                employeeReportItem.setPercent("100%");
                employeeReportItem.setJobTarget(summary);
                employeeReportItem.setTotalHours(hours);
                reportItems.add(employeeReportItem);
            });
        });
        return reportItems;
    }

    @SneakyThrows
    @Override
    public List<EmployeeReportItemDTO> getWeekReportjiraIssues(Date date) {
        DateTime dateTime = new DateTime(date);
        DateTime monday = dateTime.withDayOfWeek(DateTimeConstants.MONDAY);
        DateTime friday = dateTime.plusDays(5);
        return this.getDateEmployeeReportItems(monday.toDate(), friday.toDate());
    }
}



