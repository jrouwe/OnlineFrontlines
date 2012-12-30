<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Continue Game"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CONTINUE GAME"/>
</jsp:include>    

<div id="content_center_single_col">
	<c:choose>
		<c:when test="${empty games}">
			<ofl:Box title="NO GAMES" className="466">
				<p>You are currently not playing any games. Please go to the <a href="${applicationScope.appUrl}/WorldMap.do">world map</a> or <a href="${applicationScope.appUrl}/PBMCreateInvitation.do">invite a friend</a>.</p>
			</ofl:Box>
		</c:when>
		<c:otherwise>
			<div class="sub_title_466">
				CONTINUE GAME
			</div>
			<c:forEach items="${games}" var="g">
				<div class="tv_map_preview">
					<div class="tv_preview_image">				
						<img src="${applicationScope.imagesUrl}/map_${g.mapId}.png" alt="Map preview"/>
					</div>
					
					<div class="tv_preview_stats">
						<table class="stable">
							<col width="40%"/>
							<col width="60%"/>
							<tr><td class="slabel">MAP NAME:</td><td class="svalue"><c:out value="${g.countryConfigName}"/></td></tr>
							<tr><td class="slabel">PLAYER 1:</td><td class="svalue"><a class="${g.player1CanPerformAction? "game_continue_your_turn" : "game_continue_other_turn"}" href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${g.player1Id}"/></c:url>"><c:out value="${g.player1Name}"/></a></td></tr>
							<tr><td class="slabel">PLAYER 2:</td><td class="svalue"><a class="${g.player2CanPerformAction? "game_continue_your_turn" : "game_continue_other_turn"}" href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${g.player2Id}"/></c:url>"><c:out value="${g.player2Name}"/></a></td></tr>
							<tr><td class="slabel">TURN:</td><td class="svalue">${g.hasGameEnded? "Game Ended" : (g.turnNumber == 0? "Deployment" : g.turnNumber)}</td></tr>
						</table>
						
						<div style="float: left; clear: left; width: 100%;">
							<ofl:form action="GamePlay" method="get">
							    <ofl:hidden name="gameId" value="${g.id}"/>
							    <ofl:submit id="continue${g.id}" key="continue"/>
							</ofl:form>
						</div>
					</div>
				</div>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
