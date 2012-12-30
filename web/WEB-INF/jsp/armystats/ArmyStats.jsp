<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="onlinefrontlines.*" %>
<%@ page import="onlinefrontlines.armystats.*" %>

<%
	Army army = Army.fromInt(Integer.parseInt(request.getParameter("army")));
	ArmyStats stats = ArmyStatsDAO.getStats(army);
%>

<div class="sub_title_260_dark">
	<% if (army == Army.red) { %>
		RED ARMY
	<% } else { %>
		BLUE ARMY
	<% } %>
</div>

<% if (army == Army.red) { %>
	<div id="home_red">
<% } else { %>
	<div id="home_blue">
<% } %>
	<table class="stable">
		<col width="70%"/>
		<col width="30%"/>
		<tr><td class="slabel">Members:</td><td class="svalue"><%= stats.members %></td></tr>
		<tr><td class="slabel">Games Played:</td><td class="svalue"><%= stats.gamesPlayed %></td></tr>
		<tr><td class="slabel">Games Won:</td><td class="svalue"><%= stats.gamesWon %></td></tr>
		<tr><td class="slabel">Countries Owned:</td><td class="svalue"><%= stats.countriesOwned %></td></tr>
	</table>
</div>
