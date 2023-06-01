**以下同事存在过期任务或者缺陷, 请确认是否存在风险**
<#list users as user>
    @${user.mobile} : <#list user.jiraIds as jiraId>[${jiraId}](http://jira.bsfit.com.cn:8080/browse/${jiraId}), </#list>  <br/>
</#list>