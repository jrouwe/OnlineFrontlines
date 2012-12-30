<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Edit Deployment"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:form action="DoDeploymentEdit">
		<ofl:Box title="EDIT DEPLOYMENT" className="466" style="height: 348px; overflow-y: scroll">
			<ofl:actionerror/>
	
		    <ofl:textfield key="deploymentName" value="${deploymentName}" maxlength="32"/>
			<c:forEach items="${deployment}" var="d">
			    <ofl:textfield key="${d.name}" name="unit${d.id}" value="${d.amount}"/>
			</c:forEach>
			<ofl:hidden name="deploymentId" value="${deploymentId}"/>
		</ofl:Box>

		<ofl:Box className="466">
			<div>Unit count: ${deploymentConfig.totalUnits}, victory points: ${deploymentConfig.totalVictoryPoints}</div>
			<ofl:submit key="submit"/>
		</ofl:Box>
	</ofl:form>
	
	<ofl:Box title="ACTIONS" className="466">
		<table class="gtable">
			<ofl:Go title="List Deployments">${applicationScope.appUrl}/DeploymentList.do</ofl:Go>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
