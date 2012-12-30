<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="onlinefrontlines.utils.Tools" %>
<%@include file="mailtop.jspf"%>
	Hello <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"recipientName\")) %>"/>,<br/>
	A game has been created, you can deploy your units!
<%@include file="mailboxbottom.jspf"%>
<%@include file="mailboxtop.jspf"%>
	<a href="<%= Tools.decodeGetParameter(request.getParameter("link")) %>" style="<%=href%>">Click here to go to the game.</a>
<%@include file="mailbottom.jspf"%>