/**
 * Copyright © 2010 浙江邦盛科技有限公司 版权所有
 */
package com.alvin.jira.dto;

import lombok.Data;

/**
 * 类的描述
 *
 * @author alvin
 * @version 7.x
 * @since 2023/6/4
 **/
@Data
public class EmployeeReportItemDTO {

    private String name;

    private String cate;

    private String group;

    private String jobTarget;

    private String percent;

    private Integer totalHours;
}
