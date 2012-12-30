<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Reply to Feedback"/>
	<jsp:param name="mainMenuSelection" value="PROFILE"/>
	<jsp:param name="subMenuSelection" value="RECEIVED FEEDBACK"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="REPLY TO FEEDBACK" className="466">
		<ofl:actionerror/>
		
		<ofl:form action="DoFeedbackReply">
			<ofl:hidden name="feedbackId" value="${feedbackId}"/>
		    <ofl:textarea key="reply" value="${reply}" cols="40" rows="10"/>
		    <ofl:submit key="submit"/>
		</ofl:form>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
