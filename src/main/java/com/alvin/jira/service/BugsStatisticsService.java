package com.alvin.jira.service;

import net.rcarz.jiraclient.JiraException;

/**
 * <p>bug统计服务类</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/16  17:49
 */
public interface BugsStatisticsService {
    /**
     * 通知未登记bug原因的人员
     *
     * @throws JiraException
     */
    void notifyUnlogBugReason() throws JiraException;

    /**
     * 通知本周缺陷数量
     */
    void notifyCurrentWeekUserBugNum();
}
