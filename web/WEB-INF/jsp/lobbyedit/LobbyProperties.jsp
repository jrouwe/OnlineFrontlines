<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Lobby Properties"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="EDIT"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="LOBBY PROPERTIES" className="466">
		<ofl:actionerror/>
		
		<ofl:form action="DoLobbyProperties">
			<ofl:label key="lobbyName" value="${lobbyName}"/>
		    <ofl:textfield key="backgroundImageNumber" value="${backgroundImageNumber}"/>
		    <ofl:textfield key="worldMapEnterButtonX" value="${worldMapEnterButtonX}"/>
		    <ofl:textfield key="worldMapEnterButtonY" value="${worldMapEnterButtonY}"/>
		    <ofl:textfield key="minRequiredLevel" value="${minRequiredLevel}"/>
		    <ofl:textfield key="maxLevel" value="${maxLevel}"/>
		    <ofl:textfield key="maxUsers" value="${maxUsers}"/>
			<ofl:hidden name="lobbyId" value="${lobbyId}"/>
		    <ofl:submit key="submit"/>
		</ofl:form>
	</ofl:Box>
	
	<p><a href="${applicationScope.appUrl}/LobbyList.do">Lobby List</a></p>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
