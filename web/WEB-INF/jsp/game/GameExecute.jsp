<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Execute Action"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="EXECUTE"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="EXECUTE ACTION" className="466">
		<ofl:form action="GameDoExecute">
		    <ofl:textfield key="gameId" value="${gameId}"/>
		    <ofl:textarea key="requestedActions" value="${requestedActions}" cols="30" rows="20"/>
		    <ofl:textfield key="numActionsToExecute" value="${numActionsToExecute}"/>
		    <ofl:textfield key="delayBetweenActions" value="${delayBetweenActions}"/>
		    <ofl:submit key="submit"/>
		</ofl:form>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
