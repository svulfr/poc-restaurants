<#-- @ftlvariable name="menu" type="java.util.List<ru.ulfr.poc.restaurants.modules.restaurants.model.Restaurant>" -->
<#-- @ftlvariable name="account" type="ru.ulfr.poc.restaurants.modules.account.model.Account" -->
<html>
<head>
    <title>index</title>
</head>
<body>
<#if account??>
<p>Current user: ${account.login}</p>

<form method="post" action="/logout">
    <input type="submit" value="Log out">
</form>
<#else>
<p>Guest visitor. <a href="/login">Log in</a></p>
</#if>
<#list menu as restaurant>
<h4>${restaurant.name}</h4>
<i>Votes: ${restaurant.votes}, rating last week: ${(restaurant.rating!0)/7}</i>
    <#list restaurant.dishes as dish>
    <p>${dish.name} - ${dish.price?string.currency}</p>
    </#list>
    <#if account??>
    <form method="post" action="/rest/restaurants/${restaurant.id?c}/vote">
        <input type="submit" value="vote!">
    </form>
    </#if>
</#list>
</body>
</html>