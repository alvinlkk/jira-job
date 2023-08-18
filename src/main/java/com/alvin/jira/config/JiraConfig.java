/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alvin.jira.config.properties.DingTalkProperties;
import com.alvin.jira.config.properties.JiraAuthProperties;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.JiraClient;

/**
 * Jira配置类
 *
 * @author alvin
 * @version 7.x
 * @since 2023/8/12
 **/
@Configuration
@EnableConfigurationProperties({JiraAuthProperties.class, DingTalkProperties.class})
public class JiraConfig {

    @Autowired
    private JiraAuthProperties jiraAuthProperties;

    /**
     * jira client的bean
     * @return
     */
    @Bean
    public JiraClient createJiraClient() {
        BasicCredentials creds = new BasicCredentials(jiraAuthProperties.getUsername(), jiraAuthProperties.getPassword());
        JiraClient jiraClient = new JiraClient(jiraAuthProperties.getUrl(), creds);
        return jiraClient;
    }

}
