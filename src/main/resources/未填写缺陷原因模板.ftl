**以下同事关闭缺陷未登记原因分类** <br/>
<#list users as user>
    @${user.mobile} : <#list user.jiraIds as jiraId>[${jiraId}](http://jira.bsfit.com.cn:8080/browse/${jiraId}), </#list>  <br/>
</#list>