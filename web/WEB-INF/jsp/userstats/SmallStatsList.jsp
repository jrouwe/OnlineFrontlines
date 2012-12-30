<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<c:set var="line" value="1"/>
<table class="ltable">
	<col width="10%"/>
	<col width="9%"/>
	<col width="15%"/>
	<col width="9%"/>
	<col width="17%"/>
	<col width="40%"/>
	<c:forEach items="${entries}" var="e" end="10">
		<tr>
			<td class="lline${line}_left">${e.position}</td>
			<td class="lline${line}"><img src="${applicationScope.assetsUrl}/user_icons/${e.army}.png" alt="${e.army} icon"/></td>
			<td class="lline${line}">${e.statValue}${param.unit}</td>
			<td class="lline${line}"><img src="${applicationScope.assetsUrl}/ranks_small/rank<fmt:formatNumber minIntegerDigits="2" value="${e.rank}"/>.gif" alt="Rank ${e.rank} icon"/></td>
			<td class="lline${line}">L${e.level}</td>						
			<td class="lline${line}_right"><a class="llink" href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${e.userId}"/></c:url>"><c:out value="${e.username}"/></a></td>
		</tr>
		<c:set var="line" value="${line == 1? 2 : 1}"/>
	</c:forEach>
	<tr><td class="lline${line}" colspan="6"></td></tr>
</table>
