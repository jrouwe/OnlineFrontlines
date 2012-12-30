<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Error Page"/>
	<jsp:param name="mainMenuSelection" value="ERROR"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="ERROR" className="466">
		<ofl:actionerror/>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
