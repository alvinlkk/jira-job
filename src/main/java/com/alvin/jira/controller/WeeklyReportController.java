/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.controller;

import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alvin.jira.dto.EmployeeReportItemDTO;
import com.alvin.jira.service.JiraService;
import com.alvin.jira.service.WeeklyReportService;

import lombok.SneakyThrows;

/**
 * 类的描述
 *
 * @author alvin
 * @version 7.x
 * @since 2023/6/4
 **/
@RestController
@RequestMapping("/weekly/report")
public class WeeklyReportController {
    @Autowired
    private JiraService jiraService;

    @Autowired
    private WeeklyReportService weeklyReportService;

    @GetMapping("/detail")
    public List<EmployeeReportItemDTO> reportDetail () {
        List<EmployeeReportItemDTO> weeklyReportJiraIssues = jiraService.getWeeklyReportJiraIssues();
        return weeklyReportJiraIssues;
    }


    @SneakyThrows
    @GetMapping("/download")
    public void downloadWeeklyReport(HttpServletResponse response, @RequestParam(name = "date") String date) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("决策平台周报" + date,"UTF-8").replaceAll("\\+","%20");
        response.setHeader("Content-disposition","attachment;filename*=utf-8''"+fileName+".xlsx");
        weeklyReportService.downloadWeeklyReport(response.getOutputStream(), date);
    }
}
