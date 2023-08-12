/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 类的描述
 *
 * @author alvin
 * @version 7.x
 * @since 2023/8/12
 **/
@ConfigurationProperties(prefix = "dingtalk.auth")
@Data
public class DingTalkProperties {

    private String url;

    private String secretKey;

}
