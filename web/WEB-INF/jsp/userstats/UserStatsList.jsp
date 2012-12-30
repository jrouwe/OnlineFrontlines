<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Rankings"/>
	<jsp:param name="mainMenuSelection" value="RANKINGS"/>
	<jsp:param name="subMenuSelection" value="INDIVIDUALS"/>
</jsp:include>    

<ofl:Cache key="UserStatsList" timeToLiveSeconds="600">
	<div id="content_left">	    
		<ofl:Box title="MOST COUNTRIES" className="260" style="padding: 20px 0px 0px; height: 198px;">
			<c:set var="entries" scope="request" value="${totalCountries.entries}"/>
			<jsp:include page="/WEB-INF/jsp/userstats/SmallStatsList.jsp"/>
		</ofl:Box>
	
		<ofl:Box title="UNITS DESTROYED" className="260" style="padding: 20px 0px 0px; height: 198px;">
			<c:set var="entries" scope="request" value="${unitsDestroyed.entries}"/>
			<jsp:include page="/WEB-INF/jsp/userstats/SmallStatsList.jsp"/>
		</ofl:Box>
	</div>
	
	<div class="vertical_divider">
	</div>
	
	<div id="content_center">					
		<ofl:Box title="TOTAL POINTS" className="466" style="height: 471px; padding: 10px 0px 0px 0px;">
			<c:set var="line" value="1"/>
			<table class="ltable">
				<col width="6%"/>
				<col width="4%"/>
				<col width="15%"/>
				<col width="10%"/>
				<col width="5%"/>
				<col width="60%"/>
				<c:forEach items="${totalPoints.entries}" var="e" end="30">
					<tr>
						<td class="lline${line}_left">${e.position}</td>
						<td class="lline${line}"><img src="${applicationScope.assetsUrl}/user_icons/${e.army}.png" alt="${e.army} icon"/></td>
						<td class="lline${line}">${e.statValue}</td>
						<td class="lline${line}">L${e.level}</td>
						<td class="lline${line}"><img src="${applicationScope.assetsUrl}/ranks_small/rank<fmt:formatNumber minIntegerDigits="2" value="${e.rank}"/>.gif" alt="Rank ${e.rank} icon"/></td>
						<td class="lline${line}_right"><a class="llink" href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${e.userId}"/></c:url>"><c:out value="${e.username}"/></a></td>
					</tr>
					<c:set var="line" value="${line == 1? 2 : 1}"/>
				</c:forEach>
				<tr><td class="lline${line}" colspan="6"></td></tr>
			</table>
		</ofl:Box>
	</div>
	
	<div class="vertical_divider">
	</div>
	
	<div id="content_right">
		<ofl:Box title="WIN PERCENTAGE" className="260" style="padding: 20px 0px 0px; height: 198px;">
			<c:set var="entries" scope="request" value="${winPercentage.entries}"/>
			<jsp:include page="/WEB-INF/jsp/userstats/SmallStatsList.jsp">
				<jsp:param name="unit" value="%"/>
			</jsp:include>
		</ofl:Box>
	
		<ofl:Box title="CAPTURE POINTS CAPTURED" className="260" style="padding: 20px 0px 0px; height: 198px;">
			<c:set var="entries" scope="request" value="${totalCaptures.entries}"/>
			<jsp:include page="/WEB-INF/jsp/userstats/SmallStatsList.jsp"/>
		</ofl:Box>
	</div>
</ofl:Cache>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
