<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Deployment List"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="DEPLOYMENT LIST" className="466" style="height: 375px; overflow-y: scroll">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Name</th>
				<th>Edit</th>
				<th>Delete</th>
			</tr>
			<c:forEach items="${deployments}" var="d">
				<tr>
					<td class="lline${line}_left"><c:out value="${d.name}"/></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/DeploymentEdit.do"><c:param name="deploymentId" value="${d.id}"/></c:url>">Edit</a></td>
					<td class="lline${line}_right"><a href="<c:url value="${applicationScope.appUrl}/DeploymentDelete.do"><c:param name="deploymentId" value="${d.id}"/></c:url>" onclick="return confirm('Are you sure you want to delete this deployment?')">Delete</a></td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="3"></td></tr>
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
