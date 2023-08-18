package com.alvin.jira.manager;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alvin.jira.config.properties.DingTalkProperties;
import com.alvin.jira.dto.UserIssuesDTO;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  14:23
 */
@Slf4j
@Component
public class DingTalkNotifyManager {

    @Autowired
    private DingTalkProperties dingTalkProperties;

    private String genDingTalkUrl() {
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + dingTalkProperties.getSecretKey();
            Mac mac = null;
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(dingTalkProperties.getSecretKey().getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            String urlTmpl = dingTalkProperties.getUrl() + "&timestamp={}&sign={}";
            return StrUtil.format(urlTmpl, timestamp, sign);
        } catch (Exception e) {
            log.error("getDingTalkUrl error", e);
        }
        return null;
    }


    public void sendText(String content, List<String> mobiles) {
        String url = genDingTalkUrl();
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiRobotSendRequest req = new OapiRobotSendRequest();
        req.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        text.setContent(content);
        req.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(mobiles);
        req.setAt(at);
        try {
            OapiRobotSendResponse rsp = client.execute(req, "");
            if(!rsp.isSuccess()) {
                log.error("rsp error, errorCode: [{}], errorMsg: [{}]", rsp.getErrcode(), rsp.getErrmsg());
            };

        } catch (ApiException e) {
            log.error("sendText error", e);
        }
    }

    /**
     * 发送markdown内容
     * @param title 标题
     * @param content 内容
     * @param mobiles 手机号，需要@的人
     */
    public void sendMarkDown(String title, String content, List<String> mobiles) {
        String url = genDingTalkUrl();
        DingTalkClient client = new DefaultDingTalkClient(url);
        OapiRobotSendRequest req = new OapiRobotSendRequest();
        req.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        markdown.setText(content);
        req.setMarkdown(markdown);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(mobiles);
        req.setAt(at);
        try {
            OapiRobotSendResponse rsp = client.execute(req, "");
            if(!rsp.isSuccess()) {
                log.error("rsp error, errorCode: [{}], errorMsg: [{}]", rsp.getErrcode(), rsp.getErrmsg());
            };

        } catch (ApiException e) {
            log.error("sendText error", e);
        }
    }
}
