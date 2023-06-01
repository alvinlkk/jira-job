package com.alvin.jira.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;

/**
 * <p>描 述：</p>
 *
 * @author cxw (332059317@qq.com)
 * @version 1.0.0
 * @since 2023/6/1  10:23
 */
public enum UserEnum {

    CXW("cxw", "陈旭伟", "dev", "15558007607"),
    YZW("yuzw", "余志伟", "dev", "17857093638"),
    XLJ("xuanlj", "宣林杰", "dev", "18657113845"),
    LF("lufan", "卢凡", "dev", "17816875536"),
    JQJ("jiangqijie", "蒋琦杰", "dev", "13777881457"),
    NJL("niejl", "聂嘉梁", "dev", "18698578696"),
    LYZ("liyz", "李元智", "dev", "15270032962"),
    LY("liyang", "李杨", "dev", "15710403550"),
    CZR("chengzr", "程峥嵘", "dev", "15568928265"),
    CS("chensen", "陈森", "dev", "17816096717"),
    QJ("qiangj", "强瑾", "dev", "17393165662"),
    LH("lingh", "凌湖", "dev", "18055604480"),
    ZMS("zhangms", "章敏松", "dev", "17857072843"),
    WZP("weizp", "卫志鹏", "dev", "18169661319"),
    CJ("chenj", "陈杰", "dev", "13777823073"),
    XJY("xiajy", "夏靖宇", "dev", "15856992420"),
    ZX("zhangx", "张笑", "dev", "18291072670"),
    QZW("qizw", "戚泽威", "dev", "15757386707"),
    XMM("xumm", "徐萌萌", "dev", "18910260525"),
    GRT("guort", "郭荣婷", "dev", "17823886624");

    private String userName;

    private String realName;

    private String userType;

    private String mobile;

    UserEnum(String userName, String realName, String userType, String mobile) {
        this.userName = userName;
        this.realName = realName;
        this.userType = userType;
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public static List<String> getAllUserNames() {
        return Arrays.stream(UserEnum.values()).map(UserEnum::getUserName).collect(Collectors.toList());
    }

    public static String getMobileByUserName(String userName) {
        Optional<UserEnum> userEnumOp = Arrays.stream(UserEnum.values()).filter(item -> StrUtil.equals(userName, item.getUserName())).findFirst();
        if(userEnumOp.isPresent()) {
            return userEnumOp.get().mobile;
        };
        return null;
    }
}
