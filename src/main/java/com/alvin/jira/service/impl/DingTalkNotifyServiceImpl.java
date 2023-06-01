package com.alvin.jira.service.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alvin.jira.dto.UserExpireIssuesDTO;
import com.alvin.jira.enums.UserEnum;
import com.alvin.jira.manager.DingTalkNotifyManager;
import com.alvin.jira.service.DingTalkNotifyService;
import com.alvin.jira.service.JiraService;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  16:59
 */
@Service
public class DingTalkNotifyServiceImpl implements DingTalkNotifyService {

    @Autowired
    private JiraService jiraService;


    @Override
    public void notifyTodayUncreateTasks() {
        List<UserEnum> todayUnCreatedTaskUsers = jiraService.getTodayUnCreatedTaskUsers();
        if(CollUtil.isEmpty(todayUnCreatedTaskUsers)) {
            return;
        }

        notifyUnCreateTaskUsers("当日Jira任务未创建.txt", todayUnCreatedTaskUsers);
    }

    @Override
    public void notifyNextWeekUnCreateTasks() {
        List<UserEnum> unCreatedTaskUsers = jiraService.getNextWeekUnCreatedTaskUsers();
        if(CollUtil.isEmpty(unCreatedTaskUsers)) {
            return;
        }
        notifyUnCreateTaskUsers("下周任务未创建.txt", unCreatedTaskUsers);
    }

    private void notifyUnCreateTaskUsers(String name, List<UserEnum> unCreatedTaskUsers) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(name);
        String textTmpl = IoUtil.readUtf8(inputStream);
        List<String> mobiles = unCreatedTaskUsers.stream().map(UserEnum::getMobile).collect(Collectors.toList());
        String atMobiles = mobiles.stream().map(item -> "@" + item).collect(Collectors.joining(" "));
        Map<String, String> params = new HashMap<>();
        params.put("atMobiles", atMobiles);
        String text = StrUtil.format(textTmpl, params);
        DingTalkNotifyManager.sendText(text, mobiles);
    }

    @SneakyThrows
    @Override
    public void notifyExpireTasks() {
        Map<String, List<String>> userExpireIssues = jiraService.getUserExpireIssues();
        if(CollUtil.isEmpty(userExpireIssues)) {
            return;
        }

        List<UserExpireIssuesDTO> userExpireIssuesDtos = new ArrayList<>();
        userExpireIssues.forEach((username, jiraIds) -> {
            UserExpireIssuesDTO userExpireIssuesDto = new UserExpireIssuesDTO();
            String mobile = UserEnum.getMobileByUserName(username);
            userExpireIssuesDto.setMobile(mobile);
            userExpireIssuesDto.setJiraIds(jiraIds);
            userExpireIssuesDtos.add(userExpireIssuesDto);
        });
        TemplateLoader templateLoader = new ClassTemplateLoader(this.getClass(), "/");
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        cfg.setTemplateLoader(templateLoader);;
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        Map<String, Object> params = new HashMap<>();
        params.put("users", userExpireIssuesDtos);
        Template temp = cfg.getTemplate("存在过期任务模板.ftl");
        StringWriter stringWriter = new StringWriter();
        temp.process(params, stringWriter);

        List<String> mobiles = userExpireIssuesDtos.stream().map(UserExpireIssuesDTO::getMobile).collect(Collectors.toList());
        DingTalkNotifyManager.sendMarkDown("过期任务", stringWriter.toString(), mobiles);
    }

}
