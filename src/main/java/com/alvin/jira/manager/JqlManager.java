package com.alvin.jira.manager;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  10:25
 */
public class JqlManager {

    /**
     * 获取用户任务的查询语句
     * @param users
     * @param dueDateStart
     * @param dueDateEnd
     * @return
     */
    public static String getUserTasksJql(List<String> users, String dueDateStart, String dueDateEnd) {
        String jqlTmpl = "project in (SDM, ZNJCPTYH, FEATURE) AND issuetype in (子任务-产品, \"子任务-开发(前端)\", \"子任务-开发(后端)\", 子任务-测试, 子任务-设计)  AND due >= {} AND due <= {} AND assignee in ({}) ORDER BY priority DESC";
        String jql = StrUtil.format(jqlTmpl, dueDateStart, dueDateEnd, CollUtil.join(users, ","));
        return jql;
    }

    /**
     * 获取用户过期的查询语句
     * @param users
     * @param dueDateStr
     * @return
     */
    public static String getUserExpireTasksJql(List<String> users, String dueDateStr) {
        String jqlTmpl = "resolution = Unresolved and due <= {} AND assignee in ({}) ORDER BY priority DESC";
        String jql = StrUtil.format(jqlTmpl, dueDateStr, CollUtil.join(users, ","));
        return jql;
    }

    /**
     * 获取指定时间范围内用户的缺陷和改进列表
     * @param users 用户
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    public static String getUserBugsJql(List<String> users, String startDate, String endDate) {
        String jqlTmpl = "issuetype in (Bug, 缺陷, 问题) AND created >= {} AND created <= {} AND assignee in ({})";
        return StrUtil.format(jqlTmpl, startDate, endDate, CollUtil.join(users, ","));
    }

}
