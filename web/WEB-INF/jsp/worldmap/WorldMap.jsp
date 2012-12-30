<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Game World Map"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="WORLD MAP"/>
</jsp:include>    

<div class="worldmap_background">
	<c:forEach items="${lobbies}" var="lobby">
		<c:set var="allowed" value="${userLevel >= lobby.lobbyConfig.minRequiredLevel && (lobby.lobbyConfig.maxLevel < 0 || userLevel <= lobby.lobbyConfig.maxLevel)}"/>
		<c:choose>
			<c:when test="${allowed}">
				<a href="${applicationScope.appUrl}/Lobby.do?lobbyId=${lobby.lobbyConfig.id}" class="worldmap_lobby" style="left: ${lobby.lobbyConfig.worldMapEnterButtonX}px; top: ${lobby.lobbyConfig.worldMapEnterButtonY}px;">
			</c:when>
			<c:otherwise>    
				<div class="worldmap_lobby_grey" style="left: ${lobby.lobbyConfig.worldMapEnterButtonX}px; top: ${lobby.lobbyConfig.worldMapEnterButtonY}px;">
			</c:otherwise>
		</c:choose>
			<div class="worldmap_lobbyname">${lobby.lobbyConfig.name}</div>
			<div class="worldmap_games">${lobby.gameCount}/${lobby.challengeCount}</div>
			<div class="worldmap_red" style="width: ${lobby.balance.fractionRed * 26}px;"></div>
			<div class="worldmap_blue" style="width: ${lobby.balance.fractionBlue * 26}px;"></div>
			<c:choose>
				<c:when test="${lobby.lobbyConfig.maxLevel < 0}">
					<div class="worldmap_level">${lobby.lobbyConfig.minRequiredLevel}+</div>
				</c:when>
				<c:otherwise>    
					<div class="worldmap_level">${lobby.lobbyConfig.minRequiredLevel}-${lobby.lobbyConfig.maxLevel}</div>
				</c:otherwise>
			</c:choose>
		<c:choose>
			<c:when test="${allowed}">
				</a>
			</c:when>
			<c:otherwise>    
				</div>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	
	<div class="worldmap_totalgames">${totalGames}</div>
	<div class="worldmap_totalred" style="width: ${totalFractionRed * 85}px;"></div>
	<div class="worldmap_totalblue" style="width: ${totalFractionBlue * 85}px;"></div>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
