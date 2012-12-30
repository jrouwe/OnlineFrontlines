<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Received Feedback"/>
	<jsp:param name="mainMenuSelection" value="PROFILE"/>
	<jsp:param name="subMenuSelection" value="RECEIVED FEEDBACK"/>
</jsp:include>    

<div id="content_center_single_col">
	<div class="sub_title_466">
		<c:out value="${feedbackUser.username}"/>
	</div>

	<ofl:Box className="466">
		<p>Feedback Score: ${feedbackScore}</p>

		<p>Note that one person can only contribute for &plusmn;1 point to another persons score. The last feedback given determines this contribution.</p>
	</ofl:Box>
		
	<c:if test="${!empty feedbackList}">
		<div class="sub_title_466">
			LATEST FEEDBACK
		</div>
	</c:if>

	<c:forEach items="${feedbackList}" var="f">
		<ofl:Box className="466">
			<div class="wwlbl">Game:</div><div class="wwctrl"><a href="<c:url value="${applicationScope.appUrl}/GameReplay.do"><c:param name="gameId" value="${f.gameId}"/></c:url>">View Game</a></div>
			<div class="wwlbl">Date:</div><div class="wwctrl">${f.creationDateString}</div>
			<div class="wwlbl">Opponent:</div><div class="wwctrl"><a href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${f.reporterUserId}"/></c:url>"><c:out value="${f.reporterUsername}"/></a></div>
			<div class="wwlbl">Score:</div><div class="wwctrl">${f.score}</div>
			<div class="wwlbl">Comments:</div><div class="wwctrl"><c:out value="${f.comments}"/></div>
			<c:choose>
				<c:when test="${f.reply == null && user != null && f.opponentUserId == user.id}">
					<div class="wwlbl">Reply:</div><div class="wwctrl"><a href="<c:url value="${applicationScope.appUrl}/FeedbackReply.do"><c:param name="feedbackId" value="${f.id}"/></c:url>">Click here to reply</a></div>
				</c:when>
				<c:otherwise>
					<div class="wwlbl">Reply:</div><div class="wwctrl"><c:out value="${f.reply}" default="-"/></div>
				</c:otherwise>
			</c:choose>
		</ofl:Box>
	</c:forEach>

	<ofl:Box title="ACTIONS" className="466" style="padding: 10px 0px 0px 0px;">
		<table class="gtable">
			<ofl:Go title="View Full Profile"><c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${userId}"/></c:url></ofl:Go>					
		</table>
	</ofl:Box>
</div>  

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
