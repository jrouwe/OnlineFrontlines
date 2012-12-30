<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Game Results"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="RESULTS"/>
</jsp:include>    

<h2>Results by Country</h2>

<table class="ptable">
	<tr>
		<th>Id</th>
		<th>Name</th>
		<th>In Progress</th>
		<th>Faction 1 Wins</th>
		<th>Faction 2 Wins</th>
		<th>Draws</th>
		<th>Total</th>
	</tr>
	
	<c:forEach items="${byCountry}" var="c">
		<tr>
			<td>${c.countryConfigId}</td>
			<td><c:out value="${c.countryConfigName}"/></td>
			<td>${c.inProgress}</td>
			<td>${c.f1Wins}</td>
			<td>${c.f2Wins}</td>
			<td>${c.draws}</td>
			<td>${c.total}</td>
		</tr>
	</c:forEach>	
</table>

<h2>Results by Map</h2>

<table class="ptable">
	<tr>
		<th>Id</th>
		<th>Name</th>
		<th>In Progress</th>
		<th>Faction 1 Wins</th>
		<th>Faction 2 Wins</th>
		<th>Draws</th>
		<th>Total</th>
	</tr>
	
	<c:forEach items="${byMap}" var="m">
		<tr>
			<td>${m.mapId}</td>
			<td><c:out value="${m.mapName}"/></td>
			<td>${m.inProgress}</td>
			<td>${m.f1Wins}</td>
			<td>${m.f2Wins}</td>
			<td>${m.draws}</td>
			<td>${m.total}</td>
		</tr>
	</c:forEach>	
</table>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
