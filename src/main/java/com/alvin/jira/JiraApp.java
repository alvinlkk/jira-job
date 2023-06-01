package com.alvin.jira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>jira任务启动类：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  17:11
 */
@SpringBootApplication
@EnableScheduling
public class JiraApp {

    public static void main(String[] args) {
        SpringApplication.run(JiraApp.class, args);
    }
}
