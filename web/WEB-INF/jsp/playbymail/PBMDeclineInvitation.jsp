<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Decline Invitation"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="SHOW INVITATIONS"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="INVITATION DECLINED" className="466">
		Invitation declined.
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
