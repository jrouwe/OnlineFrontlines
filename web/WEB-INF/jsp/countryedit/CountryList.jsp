<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="List Countries"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="COUNTRY LIST" className="466" style="height: 375px; overflow-y: scroll">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Name</th>
				<th>Edit</th>
				<th>Delete</th>
				<th>Publish</th>
			</tr>
			<c:forEach items="${configs}" var="c">
				<tr>
					<td class="lline${line}_left"><c:out value="${c.name}"/></td>
					<td class="lline${line}">
						<c:if test="${c.publishStateAsInt == 0 || user.isAdmin}">
							<a href="<c:url value="${applicationScope.appUrl}/CountryEdit.do"><c:param name="countryConfigId" value="${c.id}"/></c:url>">Edit</a>
						</c:if>
					</td>
					<td class="lline${line}">
						<c:if test="${c.publishStateAsInt == 0 || user.isAdmin}">
							<a href="<c:url value="${applicationScope.appUrl}/CountryDelete.do"><c:param name="countryConfigId" value="${c.id}"/></c:url>" onclick="return confirm('Are you sure you want to delete this country?')">Delete</a>
						</c:if>
					</td>
					<td class="lline${line}_right">
						<c:choose>
							<c:when test="${c.publishStateAsInt == 1}">
								Requested
							</c:when>
							<c:when test="${c.publishStateAsInt == 2}">
								Published
							</c:when>
							<c:otherwise>
								<a href="<c:url value="${applicationScope.appUrl}/CountryPublish.do"><c:param name="countryConfigId" value="${c.id}"/></c:url>" onclick="return confirm('Are you sure you want to publish this country? You cannot change the country, map and deployments after you do this.')">Publish</a>
							</c:otherwise>
						</c:choose>
					</td>
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
