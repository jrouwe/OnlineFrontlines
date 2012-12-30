<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Create Country"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="CREATE COUNTRY" className="466">
		<ofl:actionerror/>
		
		<ofl:form action="DoCountryCreate">
		    <ofl:textfield key="countryConfigName" value="${countryConfigName}" maxlength="32"/>
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
