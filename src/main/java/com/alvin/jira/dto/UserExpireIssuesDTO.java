package com.alvin.jira.dto;

import java.util.List;

import com.alvin.jira.enums.UserEnum;

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
public class UserExpireIssuesDTO {

    private String mobile;

    private List<String> jiraIds;
}
