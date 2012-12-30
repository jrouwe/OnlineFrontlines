<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="onlinefrontlines.help.*" %>

<%! int numScreens = 7; %>

<% Random r = new Random(); %>

<%
	Tips.Entry tip1 = null, tip2 = null;

   	int numTips = Tips.getInstance().tips.size();
   	if (numTips > 0)
   	{    	
    	int nr1 = Math.abs(r.nextInt()) % numTips;
    	
    	int nr2 = Math.abs(r.nextInt()) % numTips;
    	if (nr1 == nr2) nr2 = (nr2 + 1) % numTips;
    	
		tip1 = Tips.getInstance().tips.get(nr1);
		tip2 = Tips.getInstance().tips.get(nr2);
   	}
%>

<ofl:Box title="TIP" className="260_dark" style="height: 87px">
	<% if (tip1 != null) { %>
		<% if (tip1.image != null) { %>
			<div class="tip_image">
				<img src="${applicationScope.assetsUrl}/<%= tip1.image %>" alt="tip image"/>
			</div>
			<div class="tip_image_text">
				<%= tip1.text %>
			</div>
		<% } else { %>
			<div class="tip_text">
				<%= tip1.text %>
			</div>
		<% } %>
	<% } %>
</ofl:Box>

<ofl:Box title="TIP" className="260_dark" style="height: 86px">
	<% if (tip2 != null) { %>
		<% if (tip2.image != null) { %>
			<div class="tip_image">
				<img src="${applicationScope.assetsUrl}/<%= tip2.image %>" alt="tip image"/>
			</div>
			<div class="tip_image_text">
				<%= tip2.text %>
			</div>
		<% } else { %>
			<div class="tip_text">
				<%= tip2.text %>
			</div>
		<% } %>
	<% } %>
</ofl:Box>
	