package com.alvin.jira.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.Lists;

import com.alvin.jira.enums.UserEnum;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import net.rcarz.jiraclient.Issue;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  17:51
 */
@Data
public class UserIssuesDTO {

    private String mobile;

    private List<String> jiraIds;


    public static List<UserIssuesDTO> buildUserIssuesFromIssues(List<Issue> issues) {
        if(CollUtil.isEmpty(issues)) {
            return new ArrayList<>();
        }
        Map<String, List<String>> userJiraIdsMap = issues.stream()
                .collect(Collectors.groupingBy(issue -> issue.getAssignee().getName(),
                        Collectors.mapping(Issue::getKey, Collectors.toList())));
        List<UserIssuesDTO> userIssues = new ArrayList<>();
        userJiraIdsMap.forEach((username, jiraIds) -> {
            UserIssuesDTO userExpireIssuesDto = new UserIssuesDTO();
            String mobile = UserEnum.getMobileByUserName(username);
            userExpireIssuesDto.setMobile(mobile);
            userExpireIssuesDto.setJiraIds(jiraIds);
            userIssues.add(userExpireIssuesDto);
        });
        return userIssues;
    }
}
