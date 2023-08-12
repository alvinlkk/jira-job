/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.service;

import java.io.OutputStream;
import java.util.List;

import com.alvin.jira.dto.EmployeeReportItemDTO;

import lombok.SneakyThrows;

/**
 * 周报服务
 *
 * @author alvin
 * @version 7.x
 * @since 2023/6/4
 **/
public interface WeeklyReportService {
    /**
     * 下载周报
     * @param out 输出流
     * @param startDate 周报起始日期
     */
    void downloadWeeklyReport(OutputStream out, String startDate);

    /**
     * 获取下周报表任务
     *
     * @return
     */
    @SneakyThrows
    List<EmployeeReportItemDTO> getWeeklyReportJiraIssues();
}
