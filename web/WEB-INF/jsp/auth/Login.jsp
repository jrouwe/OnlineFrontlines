<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Login"/>
	<jsp:param name="mainMenuSelection" value="LOGIN"/>
	<jsp:param name="subMenuSelection" value="LOGIN"/>
</jsp:include>    

<div id="content_center_single_col">	
	<ofl:Box title="PLEASE LOG IN" className="466">
		This page requires you to be logged in. Click the login button in the top right corner of the screen.
	</ofl:Box>
</div>						

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
