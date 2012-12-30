<%@ page contentType="text/html; charset=UTF-8" session="false" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Game Continent Map"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="WORLD MAP"/>
</jsp:include>    

<div style="clear: left; float: left">
	<jsp:include page="/WEB-INF/jsp/IncludeSwf.jsp">
		<jsp:param name="width" value="990"/>
		<jsp:param name="height" value="595"/>
		<jsp:param name="swfPath" value="Lobby#buildNumber.swf"/>
		<jsp:param name="swfParam" value="lobbyId=${param.lobbyId}"/>
	</jsp:include>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
