**以下同事存在过期任务或者缺陷, 请下班前拖动完成的任务，或者主动上报风险** <br/>
<#list users as user>
    @${user.mobile} : <#list user.jiraIds as jiraId>[${jiraId}](http://jira.bsfit.com.cn:8080/browse/${jiraId}), </#list>  <br/>
</#list>