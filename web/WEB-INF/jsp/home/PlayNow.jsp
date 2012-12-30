<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Play Now"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
</jsp:include>    
	
<div id="content_center_single_col">
	<c:if test="${canContinueGame}">
		<div class="sub_title_466">
			CONTINUE YOUR CURRENT GAME
		</div>
	
		<div id="playnow_continue">	
			<div class="playnow_actions1">
				<table class="gtable">
					<ofl:Go title="CONTINUE GAME">${applicationScope.appUrl}/GameContinue.do</ofl:Go>
				</table>
			</div>					
		</div>
	</c:if>

	<div class="sub_title_466">
		QUICK GAME AGAINST COMPUTER
	</div>

	<div id="playnow_ai">
		<div class="playnow_actions1">
			<table class="gtable">
				<ofl:Go title="PLAY VERSUS COMPUTER">${applicationScope.appUrl}/GameCreate.do?ai=true</ofl:Go>
			</table>
		</div>					
	</div>

	<div class="sub_title_466">
		PLAY A FRIENDLY GAME
	</div>

	<div id="playnow_mail">
		<div class="playnow_actions2">
			<table class="gtable">
				<ofl:Go title="INVITE FRIEND">${applicationScope.appUrl}/PBMCreateInvitation.do</ofl:Go>
				<ofl:Go title="SHOW INVITATIONS">${applicationScope.appUrl}/PBMShowInvitation.do</ofl:Go>
			</table>
		</div>					
	</div>

	<div class="sub_title_466">
		PLAY ON THE WORLD MAP
	</div>

	<div id="playnow_ranked">					
		<div class="playnow_actions1">
			<table class="gtable">
				<ofl:Go title="WORLD MAP">${applicationScope.appUrl}/WorldMap.do</ofl:Go>
			</table>
		</div>					
	</div>
	
	<div class="sub_title_466">
		CREATE YOUR OWN LEVEL
	</div>

	<div id="playnow_custom">
		<div class="playnow_actions2">
			<table class="gtable">
				<ofl:Go title="CREATE OWN COUNTRY">${applicationScope.appUrl}/CountryCreateHome.do</ofl:Go>
				<ofl:Go title="PLAY OWN COUNTRY"><c:url value="${applicationScope.appUrl}/PBMCreateInvitation.do"><c:param name="custom" value="true"/></c:url></ofl:Go>
			</table>
		</div>					
	</div>
</div>
	
<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
