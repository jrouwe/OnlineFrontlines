<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Server Info"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
</jsp:include>    
	
<table style="text-align: left;">
	<tr>
		<td>Date:</td>
		<td>${date}</td>
	</tr>
	<tr>
		<td>OS:</td>
		<td>${os}</td>
	</tr>
	<tr>
   		<td>JVM:</td>
   		<td>${jvm}</td>
	</tr>
	<tr>
		<td>Container:</td>
		<td>${servletContainer}</td>
	</tr>
	<tr>
		<td>CPUs:</td>
		<td>${cpuCount}</td>
	</tr>
	<tr>
		<td>Memory:</td>
		<td> 
			Used: <fmt:formatNumber maxFractionDigits="2" value="${usedMemory / 1024 / 1024}"/> MB 
			(<fmt:formatNumber maxFractionDigits="1" value="${usedMemory * 100 / maxMemory}"/>%),
			Committed: <fmt:formatNumber maxFractionDigits="2" value="${committedMemory / 1024 / 1024}"/> MB,
			Max: <fmt:formatNumber maxFractionDigits="2" value="${maxMemory / 1024 / 1024}"/> MB
			[<a href="ForceGC.do">GC</a>]
		</td>
	</tr>
	<tr>
		<td>Data Source 'onlinefrontlines':</td>
		<td>${dataSourceStatus['onlinefrontlines']}</td>
	</tr>
</table> 

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
