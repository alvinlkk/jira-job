package com.alvin.jira.manager;

import java.net.URLEncoder;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  14:23
 */
@Slf4j
public class DingTalkNotifyManager {
//    private static final String SECRET_KEY = "SECd52c4ef8f5422309c3fc1ed5d8031350d7e2477be822666b1057955c226d2c4f";
//    private static final String JIRA_DD_URL = "https://oapi.dingtalk.com/robot/send?access_token=36874b8e21239126bf5006a3fb0742374cd4853c0b4634148766f0b6b4956ed9&timestamp={}&sign={}";
    private static final String JIRA_DD_URL = "https://oapi.dingtalk.com/robot/send?access_token=26b3230bcdf428d34fd69d1b58705065e8a7c6666d06fe97139e1594d0b1691c&timestamp={}&sign={}";
    private static final String SECRET_KEY = "SEC7b86b5509d3df2ecc4bc7d13b69df6437005ea8c214e8636f72ad808f353b18d";

    public static void sendText(String content, List<String> mobiles) {
        String url = getDingTalkUrl();
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

    public static void sendMarkDown(String title, String content, List<String> mobiles) {
        String url = getDingTalkUrl();
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

    private static String getDingTalkUrl() {
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + SECRET_KEY;
            Mac mac = null;
            mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            String url = StrUtil.format(JIRA_DD_URL, timestamp, sign);
            return url;
        } catch (Exception e) {
            log.error("getDingTalkUrl error", e);
        }
        return null;
    }
}
