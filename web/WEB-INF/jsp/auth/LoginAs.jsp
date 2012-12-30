<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Login"/>
	<jsp:param name="mainMenuSelection" value="LOGIN"/>
	<jsp:param name="subMenuSelection" value="LOGIN"/>
</jsp:include>    

<div id="content_center_single_col">	
	<ofl:Box title="LOGIN AS" className="466">
		<ofl:actionerror/>
		
		<ofl:form id="login_form" action="DoLoginAs">
		    <ofl:textfield key="usernameToLoginAs" value="${usernameToLoginAs}" maxlength="32"/>
		    <ofl:submit key="login" onclick="formSubmit(document.getElementById('login_form'));"/>
		</ofl:form>
	</ofl:Box>
</div>						

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
