<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Admin Page"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
</jsp:include>    
	
<div id="content_center_single_col">
	<ofl:Box title="ADMIN PAGE" className="466">
		<p><a href="${applicationScope.appUrl}/CountryListInvalid.do">List Invalid Countries</a></p>

		<p><a href="${applicationScope.appUrl}/CountryListToBePublished.do">List Countries To Be Published</a></p>

		<p><a href="${applicationScope.appUrl}/LobbyList.do">Lobby List</a></p>
		
		<p><a href="${applicationScope.appUrl}/GameConfig.do">Game Configuration</a></p>
		
		<p><a href="${applicationScope.appUrl}/GameBalance.do">Game Balance</a></p>
		
		<p><a href="${applicationScope.appUrl}/CountryBalance.do">Country Balance</a></p>

		<p><a href="${applicationScope.appUrl}/GameResults.do">Game Results</a></p>
		
		<p><a href="${applicationScope.appUrl}/GameExecute.do">Game Execute Action</a></p>
		
		<p><a href="${applicationScope.appUrl}/ServerInfo.do">Server Info</a></p>

		<p><a href="${applicationScope.appUrl}/ViewTimeAccumulators.do">View Time Accumulators</a></p>

		<p><a href="${applicationScope.appUrl}/ViewTimeSeries.do">View Time Series</a></p>

		<p><a href="${applicationScope.appUrl}/ResetProfiler.do" onclick="return confirm('Are you sure you want to reset the profile information?')">Reset Profiler</a></p>

		<p><a href="${applicationScope.appUrl}/RunUnitTests.do">Run Unit Tests</a></p>

		<p><a href="${applicationScope.appUrl}/LoginAs.do">Login As Other User</a></p>

		<p><a href="${applicationScope.appUrl}/Reset.do" onclick="return confirm('Are you sure you want to reset the application (this can break games in progress)?')">Reset Application</a></p>
		
		<%--
		<p><a href="${applicationScope.appUrl}/GenerateDummyData.do" onclick="return confirm('Are you sure you want to generate dummy data?')">Generate Dummy Data</a></p>
		--%>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
