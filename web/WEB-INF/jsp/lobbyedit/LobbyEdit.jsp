<%@ page contentType="text/html; charset=UTF-8" session="false" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Lobby Edit"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="EDIT"/>
</jsp:include>    

<jsp:include page="/WEB-INF/jsp/IncludeSwf.jsp">
	<jsp:param name="width" value="990"/>
	<jsp:param name="height" value="595"/>
	<jsp:param name="swfPath" value="LobbyEdit#buildNumber.swf"/>
	<jsp:param name="swfParam" value="lobbyId=${param.lobbyId}"/>
</jsp:include>

<p><a href="${applicationScope.appUrl}/LobbyList.do">Lobby List</a></p>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
