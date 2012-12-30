<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Map Properties"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CREATE OWN COUNTRY"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="MAP PROPERTIES" className="466">
		<ofl:actionerror/>
		
		<ofl:form action="DoMapProperties">
		    <ofl:textfield key="mapName" value="${mapName}" maxlength="32"/>
			<ofl:hidden name="mapId" value="${mapId}"/>
		    <ofl:submit key="submit"/>
		</ofl:form>
	</ofl:Box>

	<ofl:Box title="ACTIONS" className="466">
		<table class="gtable">
			<ofl:Go title="List Maps">${applicationScope.appUrl}/MapList.do</ofl:Go>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
