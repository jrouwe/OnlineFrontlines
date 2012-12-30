<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="onlinefrontlines.utils.Tools" %>
<%@include file="mailtop.jspf"%>
	Hello <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"recipientName\")) %>"/>,<br/>
	You have won the game!
<%@include file="mailboxbottom.jspf"%>
<%@include file="mailboxtop.jspf"%>
	<a href="<%= Tools.decodeGetParameter(request.getParameter("link")) %>" style="<%=href%>">Click here to view the last move.</a>
<%@include file="mailbottom.jspf"%>
	