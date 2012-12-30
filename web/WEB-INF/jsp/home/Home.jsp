<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Home"/>
	<jsp:param name="mainMenuSelection" value="HOME"/>
</jsp:include>    

<div id="content_left">
	<ofl:Cache key="left_ArmyStats" timeToLiveSeconds="120">	    
		<jsp:include page="/WEB-INF/jsp/armystats/ArmyStats.jsp">
			<jsp:param name="army" value="1"/>
		</jsp:include>    
	</ofl:Cache>
	
	<ofl:Cache key="left_MostActive" timeToLiveSeconds="86400">	    
		<ofl:Box title="MOST ACTIVE PLAYERS" className="260_dark" style="padding: 20px 0px 0px; height: 198px;">
			<c:set var="line" value="1"/>
			<table class="ltable">
				<col width="15%"/>
				<col width="55%"/>
				<col width="30%"/>
				<c:forEach items="${mostActive}" var="e" end="10">
					<tr>
						<td class="lline${line}_left">${e.position}</td>
						<td class="lline${line}"><a class="llink" href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${e.userId}"/></c:url>"><c:out value="${e.username}"/></a></td>
						<td class="lline${line}_right"><a class="llink" href="<c:url value="${applicationScope.appUrl}/PBMCreateInvitation.do"><c:param name="userId" value="${e.userId}"/></c:url>"><img class="challenge_button" src="${applicationScope.assetsUrl}/home/challenge.png" alt="challenge"/></a></td>
					</tr>
					<c:set var="line" value="${line == 1? 2 : 1}"/>
				</c:forEach>
				<tr><td class="lline${line}" colspan="4"></td></tr>
			</table>
		</ofl:Box>
	</ofl:Cache>
</div>

<div class="vertical_divider">
</div>

<ofl:Cache key="Home" timeToLiveSeconds="600">
	<div id="content_center">
		<div id="home_banner">
			Online Frontlines is a free online turn based strategy game that is played by e-mail. 
			Invite a <a href="${applicationScope.appUrl}/PBMCreateInvitation.do">friend</a> or 
			play on the <a href="${applicationScope.appUrl}/WorldMap.do">world map</a>.
		</div>
		
		<div id="playnow_button_back">
			<div id="playnow_button">
				<a href="${applicationScope.appUrl}/PlayNow.do">
					<img src="${applicationScope.assetsUrl}/home/playnow_button.png"/>
				</a>
			</div>
		</div>
	
		<ofl:Box title="NEWS" className="466" style="height: 218px; overflow: auto">
			${newsHTML}
		</ofl:Box>	
	</div>
</ofl:Cache>

<div class="vertical_divider">
</div>

<div id="content_right">
	<ofl:Cache key="right_ArmyStats" timeToLiveSeconds="120">	    
		<jsp:include page="/WEB-INF/jsp/armystats/ArmyStats.jsp">
			<jsp:param name="army" value="0"/>
		</jsp:include>    
	</ofl:Cache>
	
	<jsp:include page="/WEB-INF/jsp/home/Social.jsp"/>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
