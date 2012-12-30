<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Feedback Given"/>
	<jsp:param name="mainMenuSelection" value="PROFILE"/>
	<jsp:param name="subMenuSelection" value="GIVE FEEDBACK"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="FEEDBACK GIVEN" className="466">
		<p>Thank you for your input!</p>	
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
