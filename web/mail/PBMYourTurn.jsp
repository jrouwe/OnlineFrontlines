<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="onlinefrontlines.utils.Tools" %>
<%@include file="mailtop.jspf"%>
	Hello <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"recipientName\")) %>"/>,<br/>
	It is your turn to play!
<%@include file="mailboxbottom.jspf"%>
<%@include file="mailboxtopbig.jspf"%>
	<table style="width: 713px; margin: 5px auto; table-layout: fixed; border-spacing: 1px;">
		<col width="115"/>
		<col width="5"/>
		<col width="115"/>
		<col width="115"/>
		<col width="5"/>
		<col width="115"/>
		<col width="115"/>
		<col width="5"/>
		<col width="115"/>
		<tr>
			<td colspan="3" style="<%=th%>">Results from <c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"senderName\")) %>"/></td>
			<td colspan="3" style="<%=th%>">Current Score</td>
			<td colspan="3" style="<%=th%>">Map Info</td>
		</tr>
		<tr>
			<td style="<%=lbl%>">Destroyed Units</td>
			<td style="<%=sem%>">:</td>
			<td style="<%=val%>"><%= Tools.decodeGetParameter(request.getParameter("unitsDestroyed")) %></td>
			<td style="<%=lbl%>"><c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"p1Name\")) %>"/></td>
			<td style="<%=sem%>">:</td>
			<td style="<%=val%>"><%= Tools.decodeGetParameter(request.getParameter("p1Score")) %></td>
			<td style="<%=lbl%>">Name</td>
			<td style="<%=sem%>">:</td>
			<td style="<%=val%>"><%= Tools.decodeGetParameter(request.getParameter("countryName")) %></td>
		</tr>
		<tr>
			<td style="<%=lbl%>">Bases Conquered</td>
			<td style="<%=sem%>">:</td>
			<td style="<%=val%>"><%= Tools.decodeGetParameter(request.getParameter("basesDestroyed")) %></td>
			<td style="<%=lbl%>"><c:out value="<%= Tools.decodeGetParameter(request.getParameter(\"p2Name\")) %>"/></td>
			<td style="<%=sem%>">:</td>
			<td style="<%=val%>"><%= Tools.decodeGetParameter(request.getParameter("p2Score")) %></td>
			<td style="<%=lbl%>">Target Score</td>
			<td style="<%=sem%>">:</td>
			<td style="<%=val%>"><%= Tools.decodeGetParameter(request.getParameter("targetScore")) %></td>
		</tr>
		<tr>
			<td style="<%=lbl%>">Tiles Conquered</td>
			<td style="<%=sem%>">:</td>
			<td style="<%=val%>"><%= Tools.decodeGetParameter(request.getParameter("tilesConquered")) %></td>
		</tr>	
	</table>
<%@include file="mailboxbottombig.jspf"%>
<%@include file="mailboxtop.jspf"%>
	<a href="<%= Tools.decodeGetParameter(request.getParameter("link")) %>" style="<%=href%>">Click here to continue the game.</a>
<%@include file="mailbottom.jspf"%>
	