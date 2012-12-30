<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="onlinefrontlines.utils.Tools" %>
<%@include file="mailtop.jspf"%>
	Hello <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"recipientName\")) %>"/>,<br/>
	<c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"senderName\")) %>"/> is requesting a draw!
<%@include file="mailboxbottom.jspf"%>
<%@include file="mailboxtop.jspf"%>
	<a href="<%= Tools.decodeGetParameter(request.getParameter("link")) %>" style="<%=href%>">Click here to go to the game to accept the draw.</a> 
	Ignore this message if you don't want to accept the request. 
<%@include file="mailbottom.jspf"%>
	