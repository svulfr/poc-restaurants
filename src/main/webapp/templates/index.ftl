<#-- @ftlvariable name="menu" type="java.util.List<ru.ulfr.poc.restaurants.modules.restaurants.model.Restaurant>" -->
<#-- @ftlvariable name="account" type="ru.ulfr.poc.restaurants.modules.account.model.Account" -->
<html>
<head>
    <title>index</title>
</head>
<body>
<#if account??>
<p>Current user: ${account.login}</p>
<#else>
<p>Guest visitor</p>
</#if>
<#list menu as restaurant>
<h4>${restaurant.name}</h4>
    <#list restaurant.dishes as dish>
    <p>${dish.name} - ${dish.price?string.currency}</p>
    </#list>
</#list>
</body>
</html>