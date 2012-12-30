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
		<p>Give feedback for <a href="<c:url value="${applicationScope.appUrl}/GamePlay.do"><c:param name="gameId" value="${gameId}"/></c:url>">this</a> game against <c:out value="${opponentName}"/>.</p>
		<p>Current feedback score ${opponentScore}.</p>
		
		<ofl:actionerror/>
		
		<ofl:form action="DoFeedbackCreate">
			<ofl:hidden name="gameId" value="${gameId}"/>
			<ofl:radio key="score" list="${scoreValues}" value="${score}"/>
		    <ofl:textarea key="comments" value="${comments}" cols="40" rows="10"/>
		    <ofl:submit key="submit"/>
		</ofl:form>
		
		<p>Note that you can only contribute for &plusmn;1 point to another persons score. The last feedback that you gave determines this contribution.</p>  
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
