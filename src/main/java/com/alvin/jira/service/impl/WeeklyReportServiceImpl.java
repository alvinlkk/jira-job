/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alvin.jira.dto.EmployeeReportItemDTO;
import com.alvin.jira.manager.EmployeeManager;
import com.alvin.jira.manager.JiraManager;
import com.alvin.jira.pojo.Employee;
import com.alvin.jira.service.WeeklyReportService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.JiraException;

/**
 * 类的描述
 *
 * @author alvin
 * @version 7.x
 * @since 2023/6/4
 **/
@Service
public class WeeklyReportServiceImpl implements WeeklyReportService {

    @Autowired
    private JiraManager jiraManager;

    @Override
    public void downloadWeeklyReport(OutputStream out, String startDate) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("决策平台周报模板.xlsx");
        try (ExcelWriter excelWriter = EasyExcel.write(out).withTemplate(is).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            // 填充本周任务
            DateTime date = DateUtil.parse(startDate, "yyyy-MM-dd");
            List<EmployeeReportItemDTO> currentWeekReportItems = this.getWeekReportjiraIssues(date);
            Map<String, List<EmployeeReportItemDTO>> groupWeekReportItems = currentWeekReportItems.stream().collect(Collectors.groupingBy(EmployeeReportItemDTO::getGroup));
            groupWeekReportItems.forEach((group, items) -> {
                items = items.stream().sorted(Comparator.comparing(EmployeeReportItemDTO::getName)).collect(Collectors.toList());
                excelWriter.fill(new FillWrapper(group + "week", items), writeSheet);
            });

            // 填充下周任务
            DateTime nextWeekDate = DateUtil.offsetDay(date, 7);
            List<EmployeeReportItemDTO> nextWeekReportItems = this.getWeekReportjiraIssues(nextWeekDate);
            groupWeekReportItems = nextWeekReportItems.stream().collect(Collectors.groupingBy(EmployeeReportItemDTO::getGroup));
            groupWeekReportItems.forEach((group, items) -> {
                items = items.stream().sorted(Comparator.comparing(EmployeeReportItemDTO::getName)).collect(Collectors.toList());
                excelWriter.fill(new FillWrapper(group + "nextweek", items), writeSheet);
            });
        }
    }

    @Override
    @SneakyThrows
    public List<EmployeeReportItemDTO> getWeeklyReportJiraIssues() {
        DateTime dateTime = DateUtil.beginOfWeek(new Date());
        DateTime nextMonday = DateUtil.offsetWeek(dateTime, 1);
        DateTime nextFriday = DateUtil.offsetDay(nextMonday, 5);
        List<EmployeeReportItemDTO> reportItems = getDateEmployeeReportItems(nextMonday, nextFriday);
        return reportItems;

    }

    private List<EmployeeReportItemDTO> getDateEmployeeReportItems(Date startDate, Date endDate) throws JiraException {
        List<Issue> jiraIssues = jiraManager.getAllUserTasks(startDate, endDate);
        List<EmployeeReportItemDTO> reportItems = new ArrayList<>();
        Map<String, List<Issue>> jiraIssueGroupMap = jiraIssues.stream().collect(Collectors.groupingBy(item -> item.getAssignee().getName()));
        jiraIssueGroupMap.forEach((jiraName, issues) -> {
            Employee user = EmployeeManager.getUser(jiraName);
            if (user == null) {
                return;
            }

            Map<String, Integer> summaryHoursMap = issues.stream().collect(Collectors.groupingBy(issue -> issue.getSummary(), Collectors.summingInt(issue -> {
                Object hours = issue.getField("customfield_12100");
                if (hours == null || StrUtil.isBlank(hours.toString())) {
                    return 0;
                }
                if (StrUtil.equals("null", hours.toString())) {
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
    public List<EmployeeReportItemDTO> getWeekReportjiraIssues(Date date) {
        DateTime monday = DateUtil.beginOfWeek(date);
        DateTime friday = DateUtil.endOfWeek(date, true);
        return this.getDateEmployeeReportItems(monday, friday);
    }
}
