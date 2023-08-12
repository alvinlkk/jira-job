//package com.alvin.jira.enums;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import com.alvin.jira.pojo.Employee;
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.json.JSONUtil;
//
///**
// * <p>描 述：</p>
// *
// * @author cxw (332059317@qq.com)
// * @version 1.0.0
// * @since 2023/6/1  10:23
// */
//public enum UserEnum {
//
//    CXW("cxw", "陈旭伟", "other", "15558007607"),
//    YZW("yuzw", "余志伟", "sdm", "17857093638"),
//    XLJ("xuanlj", "宣林杰", "sdm", "18657113845"),
//    LF("lufan", "卢凡", "sdm", "17816875536"),
//    JQJ("jiangqijie", "蒋琦杰", "other", "13777881457"),
//    NJL("niejl", "聂嘉梁", "sdm", "18698578696"),
//    LYZ("liyz", "李元智", "sdm", "15270032962"),
//    LY("liyang", "李杨", "feature", "15710403550"),
//    CZR("chengzr", "程峥嵘", "feature", "15568928265"),
//    CS("chensen", "陈森", "other", "17816096717"),
//    QJ("qiangj", "强瑾", "feature", "17393165662"),
//    LH("lingh", "凌湖", "feature", "18055604480"),
//    ZMS("zhangms", "章敏松", "feature", "17857072843"),
//    WZP("weizp", "卫志鹏", "sdm", "18169661319"),
//    CJ("chenj", "陈杰", "front", "13777823073"),
//    XJY("xiajy", "夏靖宇", "front", "15856992420"),
//    ZX("zhangx", "张笑", "front", "18291072670"),
//    QZW("qizw", "戚泽威", "front", "15757386707"),
//    XMM("xumm", "徐萌萌", "front", "18910260525"),
//    XJQ("xiaojq", "萧俊全", "prod", "17601037997"),
//    CHD("chenhd", "陈昊栋", "prod", "13735870866"),
//    LRC("liurc", "刘荣灿", "prod", "17376502396"),
//    LLL("liangll", "梁丽丽", "tester", "18257346036"),
//    ZGP("zhanggp", "章关平", "tester", "18100174728"),
//    LB("lib", "李冰", "tester", "15983897556"),
//    WYY("wangyy1", "王跃跃", "tester", "18305576478"),
//    MJM("miaojm", "缪佳美", "tester", "18767105004");
//
//    private String userName;
//
//    private String realName;
//
//    private String userType;
//
//    private String mobile;
//
//    UserEnum(String userName, String realName, String userType, String mobile) {
//        this.userName = userName;
//        this.realName = realName;
//        this.userType = userType;
//        this.mobile = mobile;
//    }
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getRealName() {
//        return realName;
//    }
//
//    public void setRealName(String realName) {
//        this.realName = realName;
//    }
//
//    public String getUserType() {
//        return userType;
//    }
//
//    public void setUserType(String userType) {
//        this.userType = userType;
//    }
//
//    public String getMobile() {
//        return mobile;
//    }
//
//    public void setMobile(String mobile) {
//        this.mobile = mobile;
//    }
//
//    public static List<String> getAllUserNames() {
//        return Arrays.stream(UserEnum.values()).map(UserEnum::getUserName).collect(Collectors.toList());
//    }
//
//    public static String getMobileByUserName(String userName) {
//        Optional<UserEnum> userEnumOp = Arrays.stream(UserEnum.values()).filter(item -> StrUtil.equals(userName, item.getUserName())).findFirst();
//        if(userEnumOp.isPresent()) {
//            return userEnumOp.get().mobile;
//        };
//        return null;
//    }
//
//    public static String getRealNameByUserName(String userName) {
//        Optional<UserEnum> userEnumOp = Arrays.stream(UserEnum.values()).filter(item -> StrUtil.equals(userName, item.getUserName())).findFirst();
//        if(userEnumOp.isPresent()) {
//            return userEnumOp.get().realName;
//        };
//        return null;
//    }
//
//    public static UserEnum getUser(String userName) {
//        Optional<UserEnum> userEnumOp = Arrays.stream(UserEnum.values()).filter(item -> StrUtil.equals(userName, item.getUserName())).findFirst();
//        if(userEnumOp.isPresent()) {
//            return userEnumOp.get();
//        };
//        return null;
//    }
//
//    public static boolean isTester(String username) {
//        if(StrUtil.isEmpty(username)) {
//            return false;
//        }
//        UserEnum user = UserEnum.getUser(username);
//        if(user == null) {
//            return false;
//        }
//        return "tester".equals(user.getUserType());
//    }
//
//    public static void main(String[] args) {
//        UserEnum[] values = UserEnum.values();
//        List<Employee> users = new ArrayList<>();
//        for (UserEnum value : values) {
//            Employee user = new Employee();
//            user.setUserName(value.getUserName());
//            user.setRealName(value.getRealName());
//            user.setMobile(value.getMobile());
//            user.setUserType(value.getMobile());
//            users.add(user);
//        }
//        String json = JSONUtil.toJsonStr(users);
//        System.out.println(json);
//    }
//}
