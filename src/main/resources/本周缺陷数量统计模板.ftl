**本周已产生缺陷数量统计** <br/>
<#list users as user>
    @${user.mobile} : ${user.jiraIds?size}个  <br/>
</#list>