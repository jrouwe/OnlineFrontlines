<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="List Countries"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
</jsp:include>    

<div style="text-align: left;">${output}</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
