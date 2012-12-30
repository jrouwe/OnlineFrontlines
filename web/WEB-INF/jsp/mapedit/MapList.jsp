<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Map List"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="MAP LIST" className="466" style="height: 375px; overflow-y: scroll">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Map Name</th>
				<th>Properties</th>
				<th>Edit</th>
				<th>Delete</th>
			</tr>
			<c:forEach items="${maps}" var="m">
				<tr>
					<td class="lline${line}_left"><c:out value="${m.name}"/></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/MapProperties.do"><c:param name="mapId" value="${m.id}"/></c:url>">Properties</a></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/MapEdit.do"><c:param name="mapId" value="${m.id}"/></c:url>">Edit</a></td>
					<td class="lline${line}_right"><a href="<c:url value="${applicationScope.appUrl}/MapDelete.do"><c:param name="mapId" value="${m.id}"/></c:url>" onclick="return confirm('Are you sure you want to delete this map?')">Delete</a></td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="4"></td></tr>
		</table>
	</ofl:Box>

	<ofl:Box title="ACTIONS" className="466">
		<table class="gtable">
			<ofl:Go title="List Maps">${applicationScope.appUrl}/MapList.do</ofl:Go>
			<ofl:Go title="Create New Map">${applicationScope.appUrl}/MapCreate.do</ofl:Go>
			<ofl:Go title="List Deployments">${applicationScope.appUrl}/DeploymentList.do</ofl:Go>
			<ofl:Go title="Create New Deployment">${applicationScope.appUrl}/DeploymentCreate.do</ofl:Go>
			<ofl:Go title="List Countries">${applicationScope.appUrl}/CountryList.do</ofl:Go>
			<ofl:Go title="Create New Country">${applicationScope.appUrl}/CountryCreate.do</ofl:Go>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
