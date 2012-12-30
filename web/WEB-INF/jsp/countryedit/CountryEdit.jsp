<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Edit Country"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="EDIT COUNTRY" className="466">
		<ofl:actionerror/>

		<ofl:form action="DoCountryEdit">
		    <ofl:textfield key="countryConfigName" value="${countryConfigName}" maxlength="32"/>
			<ofl:select key="selectMapConfig" name="mapId" list="${maps}" value="${mapId}"/>
			<ofl:select key="selectCountryType" name="countryTypeId" list="${countryTypes}" value="${countryTypeId}"/>
			<ofl:select key="deploymentF1" name="deploymentConfigId1" list="${deployments}" value="${deploymentConfigId1}"/>
			<ofl:select key="deploymentF2" name="deploymentConfigId2" list="${deployments}" value="${deploymentConfigId2}"/>
		    <ofl:textfield key="scoreLimit" value="${scoreLimit}"/>
			<ofl:checkbox key="fogOfWarEnabled" value="${fogOfWarEnabled}"/>
			<c:if test="${user.isAdmin}">
				<ofl:checkbox key="isCapturePoint" value="${isCapturePoint}"/>
			    <ofl:textfield key="requiredLevel" value="${requiredLevel}" maxlength="32"/>
				<ofl:checkbox key="suitableForAI" value="${suitableForAI}"/>
			</c:if>
			<ofl:hidden name="countryConfigId" value="${countryConfigId}"/>
		    <ofl:submit key="submit"/>
		</ofl:form>
	</ofl:Box>

	<ofl:Box title="ACTIONS" className="466">
		<table class="gtable">
			<ofl:Go title="List Countries">${applicationScope.appUrl}/CountryList.do</ofl:Go>
			<ofl:Go title="Create New Map">${applicationScope.appUrl}/MapCreate.do</ofl:Go>
			<ofl:Go title="Create New Deployment">${applicationScope.appUrl}/DeploymentCreate.do</ofl:Go>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
