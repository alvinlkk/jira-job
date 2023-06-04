/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.service.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alvin.jira.dto.EmployeeReportItemDTO;
import com.alvin.jira.dto.FillData;
import com.alvin.jira.service.JiraService;
import com.alvin.jira.service.WeeklyReportService;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

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
    private JiraService jiraService;

    @Override
    public void downloadWeeklyReport(OutputStream out, String startDate) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("决策平台周报模板.xlsx");
        try (ExcelWriter excelWriter = EasyExcel.write(out).withTemplate(is).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            // 填充本周任务
            DateTime date = DateUtil.parse(startDate, "yyyy-MM-dd");
            List<EmployeeReportItemDTO> currentWeekReportItems = jiraService.getWeekReportjiraIssues(date);
            Map<String, List<EmployeeReportItemDTO>> groupWeekReportItems = currentWeekReportItems.stream().collect(Collectors.groupingBy(EmployeeReportItemDTO::getGroup));
            groupWeekReportItems.forEach((group, items) -> {
                items = items.stream().sorted(Comparator.comparing(EmployeeReportItemDTO::getName)).collect(Collectors.toList());
                excelWriter.fill(new FillWrapper(group + "week", items), writeSheet);
            });

            // 填充下周任务
            DateTime nextWeekDate = DateUtil.offsetDay(date, 7);
            List<EmployeeReportItemDTO> nextWeekReportItems = jiraService.getWeekReportjiraIssues(nextWeekDate);
            groupWeekReportItems = nextWeekReportItems.stream().collect(Collectors.groupingBy(EmployeeReportItemDTO::getGroup));
            groupWeekReportItems.forEach((group, items) -> {
                items = items.stream().sorted(Comparator.comparing(EmployeeReportItemDTO::getName)).collect(Collectors.toList());
                excelWriter.fill(new FillWrapper(group + "nextweek", items), writeSheet);
            });
        }
    }
}
