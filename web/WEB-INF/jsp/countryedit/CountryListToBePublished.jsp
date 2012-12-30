<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="List Countries To Be Published"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="COUNTRIES TO BE PUBLISHED" className="466">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Name</th>
				<th>Edit</th>
				<th>Create Game</th>
				<th>Accept</th>
				<th>Decline</th>
			</tr>
			<c:forEach items="${configs}" var="c">
				<tr>
					<td class="lline${line}_left"><c:out value="${c.name}"/></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/CountryEdit.do"><c:param name="countryConfigId" value="${c.id}"/></c:url>">Edit</a></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/DoGameCreate.do"><c:param name="selectedCountryConfig" value="${c.id}"/></c:url>">Create Game</a></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/CountryPublishAdmin.do"><c:param name="countryConfigId" value="${c.id}"/><c:param name="accept" value="true"/></c:url>">Accept</a></td>
					<td class="lline${line}_right"><a href="<c:url value="${applicationScope.appUrl}/CountryPublishAdmin.do"><c:param name="countryConfigId" value="${c.id}"/><c:param name="accept" value="false"/></c:url>">Decline</a></td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="5"></td></tr>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
