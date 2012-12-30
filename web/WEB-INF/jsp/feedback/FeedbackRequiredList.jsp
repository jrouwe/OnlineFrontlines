<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Give Feedback"/>
	<jsp:param name="mainMenuSelection" value="PROFILE"/>
	<jsp:param name="subMenuSelection" value="GIVE FEEDBACK"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="GIVE FEEDBACK" className="466">
		<c:set var="line" value="1"/>
		<table class="ltable">
			<col width="20%"/>
			<col width="40%"/>
			<col width="20%"/>
			<col width="20%"/>
			<tr>
				<th>Date</th>
				<th>Opponent</th>
				<th>Give Feedback</th>
				<th>View Game</th>
			</tr>
			<c:forEach items="${feedbackRequiredList}" var="f">
				<tr>
					<td class="lline${line}_left">${f.creationDateString}</td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${f.opponentUserId}"/></c:url>"><c:out value="${f.opponentUsername}"/></a></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/FeedbackCreate.do"><c:param name="gameId" value="${f.gameId}"/></c:url>">Give Feedback</a></td>
					<td class="lline${line}_right"><a href="<c:url value="${applicationScope.appUrl}/GamePlay.do"><c:param name="gameId" value="${f.gameId}"/></c:url>">View Game</a></td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="4"></td></tr>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
