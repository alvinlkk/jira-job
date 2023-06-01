package com.alvin.jira.service;

import java.util.List;
import java.util.Map;

import com.alvin.jira.enums.UserEnum;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  10:02
 */
public interface JiraService {


    List<UserEnum> getTodayUnCreatedTaskUsers();

    List<UserEnum> getNextWeekUnCreatedTaskUsers();

    Map<String, List<String>> getUserExpireIssues();


}
