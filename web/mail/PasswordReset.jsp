<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="onlinefrontlines.utils.Tools" %>
<%@include file="mailtop.jspf"%>
	Hello <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"recipientName\")) %>"/>,<br/>
	Your password has been reset!
<%@include file="mailboxbottom.jspf"%>
<%@include file="mailboxtop.jspf"%>
	Username: <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"username\")) %>"/><br/>
	Password: <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"password\")) %>"/>
<%@include file="mailbottom.jspf"%>
	