<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="MOTD"/>
	<jsp:param name="mainMenuSelection" value="MOTD"/>
</jsp:include>    

<ofl:Cache key="TV" timeToLiveSeconds="3600">
	<div id="content_center_single_col">
		<div class="sub_title_466">
			MATCH OF THE DAY
		</div>
	
		<div id="tv_banner_match_day">
			<div class="tv_banner_match_day_name">
				<c:choose>
					<c:when test="${topMatch.faction1IsRed}">
						<c:out value="${topMatch.username2}"/> (lvl ${topMatch.levelUser2})
					</c:when>
					<c:otherwise>
						<c:out value="${topMatch.username1}"/> (lvl ${topMatch.levelUser1})
					</c:otherwise>
				</c:choose>
			</div>
			<div class="tv_banner_match_day_name">
				<c:choose>
					<c:when test="${topMatch.faction1IsRed}">
						<c:out value="${topMatch.username1}"/> (lvl ${topMatch.levelUser1})
					</c:when>
					<c:otherwise>
						<c:out value="${topMatch.username2}"/> (lvl ${topMatch.levelUser2})
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	
		<div class="tv_map_preview">
			<div class="tv_preview_image">				
				<img src="${applicationScope.imagesUrl}/map_${topMatch.mapId}.png" alt="Map preview"/>
			</div>
			
			<div class="tv_preview_stats">
				<table class="stable">
					<col width="60%"/>
					<col width="40%"/>
					<tr><td class="slabel">MAP NAME:</td><td class="svalue"><c:out value="${topMatch.countryConfigName}"/></td></tr>
					<tr><td class="slabel">MAP TYPE:</td><td class="svalue"><c:out value="${topMatch.mapType}"/></td></tr>
					<tr><td class="slabel">NUMBER OF TURNS:</td><td class="svalue">${topMatch.turnNumber}</td></tr>
					<tr><td class="slabel">UNITS DESTROYED:</td><td class="svalue">${topMatch.unitsDestroyed}</td></tr>
				</table>
				
				<ofl:form id="tv_top_match" action="GameReplay" method="get">
				    <ofl:hidden name="gameId" value="${topMatch.gameId}"/>
				    <ofl:submit key="watch"/>
				</ofl:form>
			</div>
		</div>
	
		<ofl:Box title="RUNNER UP MATCHES" className="466" style="height: 167px">
			<table class="stable">
				<col width="30%"/>
				<col/>
				<col width="22px"/>
				<col width="10%"/>
				<c:forEach items="${matches}" var="m" begin="1" end="8">
					<tr>
						<td class="slabel"><c:out value="${m.username1}"/> vs <c:out value="${m.username2}"/></td>
						<td class="svalue">Map: <c:out value="${m.countryConfigName}"/> / Turns: <c:out value="${m.turnNumber}"/></td>
						<td class="go_arrow"></td>
						<td class="go_button"><a href="<c:url value="${applicationScope.appUrl}/GameReplay.do"><c:param name="gameId" value="${m.gameId}"/></c:url>">GO</a></td>
					</tr>
				</c:forEach>
			</table>		
		</ofl:Box>
	</div>
</ofl:Cache>
	
<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
