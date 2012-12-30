<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Create Lobby"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="CREATE"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="CREATE LOBBY" className="466">
		<ofl:actionerror/>
		
		<ofl:form action="DoLobbyCreate">
		    <ofl:textfield key="lobbyName" value="${lobbyName}" maxlength="32"/>
		    <ofl:submit key="submit"/>
		</ofl:form>
	</ofl:Box>

	<p><a href="${applicationScope.appUrl}/LobbyList.do">Lobby List</a></p>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
