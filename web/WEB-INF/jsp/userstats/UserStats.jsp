<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Profile"/>
	<jsp:param name="mainMenuSelection" value="PROFILE"/>
	<jsp:param name="subMenuSelection" value="PROFILE"/>
</jsp:include>    

<script type="text/javascript" src="wz_tooltip.js"></script>

<div id="content_left">
	<ofl:Box title="GENERAL INFO" className="260" style="height: 198px; padding: 20px 0px 0px 0px">
		<div id="userstats_icons">
			<div class="userstats_icon">
				<img src="${userImage}" alt="Avatar"/>
			</div>
			<div class="userstats_icon">
				<img src="${applicationScope.assetsUrl}/ranks_big/rank<fmt:formatNumber minIntegerDigits="2" value="${stats.rank}"/>.gif" alt="Rank"/>
			</div>
			<div class="userstats_icon">
				<img src="${applicationScope.assetsUrl}/user_icons/${returnedUser.army}_big.jpg" alt="Army"/>
			</div>
		</div>

		<table class="stable">
			<col width="30%"/>
			<col width="70%"/>
			<tr><td class="slabel">Username:</td><td class="svalue"><c:out value="${returnedUser.username}"/></td></tr>
			<tr><td class="slabel">Points:</td><td class="svalue">${stats.totalPoints}</td></tr>
			<tr><td class="slabel">Level:</td><td class="svalue">${stats.level}</td></tr>
			<tr><td class="slabel">Rank:</td><td class="svalue">${stats.rankName}</td></tr>
			<tr><td class="slabel">Feedback:</td><td class="svalue"><a class="llink" href="<c:url value="${applicationScope.appUrl}/FeedbackList.do"><c:param name="userId" value="${userId}"/></c:url>">${feedback}</a></td></tr>
		</table>
		
		<c:if test="${returnedUser.facebookId != null && (user == null || user.id != returnedUser.id)}">
			<a href="<c:url value="${applicationScope.appUrl}/PBMCreateInvitation.do"><c:param name="userId" value="${userId}"/></c:url>" class="userstats_challenge_button">
				<img src="${applicationScope.assetsUrl}/home/challenge.png" style="border: 0px" alt="Challenge"/>
			</a>
		</c:if>
	</ofl:Box>

	<ofl:Box title="PERSONAL INFO" className="260" style="height: 198px; padding: 20px 0px 0px 0px">
		<table class="stable">
			<col width="30%"/>
			<col width="70%"/>
			<tr><td class="slabel">Name:</td><td class="svalue"><c:out value="${returnedUser.realname}" default="-"/></td></tr>
			<tr><td class="slabel">Country:</td><td class="svalue"><c:out value="${returnedUser.country}" default="-"/></td></tr>
			<tr><td class="slabel">City:</td><td class="svalue"><c:out value="${returnedUser.city}" default="-"/></td></tr>
			<tr><td class="slabel">Website:</td><td class="svalue">
				<c:choose>
					<c:when test="${!empty returnedUser.website}">
						<a href="${returnedUser.websiteFullPath}" class="llink"><c:out value="${returnedUser.website}"/></a>
					</c:when>
					<c:otherwise>
						-
					</c:otherwise>
				</c:choose>
			</td></tr>
		</table>
	</ofl:Box>

	<%@include file="/WEB-INF/jsp/help/Tips.jsp"%>
</div>

<div class="vertical_divider">
</div>
	
<div id="content_center">
	<div class="sub_title_466">
		CAPTURED COUNTRIES
	</div>

	<div id="userstats_worldmap${numUnlockedContinents}">
		<c:forEach items="${ownedCountries}" var="c">
			<div class="userstats_worldmap_number" style="left: ${8 + c.lobbyConfig.worldMapEnterButtonX * 0.46}px; top: ${8 + c.lobbyConfig.worldMapEnterButtonY * 0.43}px">
				${c.count}
			</div>
		</c:forEach>	
	</div>

	<ofl:Box title="USER STATS" className="466" style="height: 198px; padding: 20px 0px 0px 0px">
		<table class="stable">
			<col width="75%"/>
			<col width="25%"/>
			<tr><td class="slabel">Games played:</td><td class="svalue">${stats.gamesPlayed}</td></tr>
			<tr><td class="slabel">Games won:</td><td class="svalue">${stats.gamesWon}</td></tr>
			<tr><td class="slabel">Games lost:</td><td class="svalue">${stats.gamesLost}</td></tr>
			<tr><td class="slabel">Current victory streak:</td><td class="svalue">${stats.currentVictoryStreak}</td></tr>
			<tr><td class="slabel">Highest victory streak:</td><td class="svalue">${stats.maxVictoryStreak}</td></tr>
			<tr><td class="slabel">Units destroyed:</td><td class="svalue">${totalKills}</td></tr>
			<tr><td class="slabel">Units lost:</td><td class="svalue">${totalDeaths}</td></tr>
			<tr><td class="slabel">Damage dealt:</td><td class="svalue">${totalDamageDealt}</td></tr>
			<tr><td class="slabel">Damage received:</td><td class="svalue">${totalDamageReceived}</td></tr>
		</table>
	</ofl:Box>

	<ofl:Box title="DAMAGE DEALT" className="466" style="height: 198px; padding: 20px 0px 0px 0px">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Unit</th>
				<th>Kills</th>
				<th># Attacks</th>
				<th>Attacks/Kill</th>
				<th>Damage</th>
				<th>Avg Damage</th>
			</tr>						
			<c:forEach items="${unitAttackStats}" var="s" end="10">
				<tr>
					<td class="lline${line}_left"><c:out value="${s.unitName}"/></td>
					<td class="lline${line}">${s.kills}</td>
					<td class="lline${line}">${s.numAttacks}</td>
					<td class="lline${line}">${s.attacksPerKillString}</td>
					<td class="lline${line}">${s.damageDealt}</td>
					<td class="lline${line}_right">${s.averageDamageString}</td>
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
	<ofl:Box title="MEDALS" className="260" style="height: 198px; padding: 20px 0px 0px 0px">
		<c:forEach items="${medals}" var="m">
	    	<div class="userstats_medal">
	    		<a href="" style="border: 0px" onmouseover="Tip('${m.medalToolTip}')" onmouseout="UnTip()" onclick="return false;">
	    			<img src="${applicationScope.assetsUrl}/medals/${m.medalImage}.gif" style="border: 0px" alt=""/>
	    		</a>
			</div>
	    </c:forEach>
	</ofl:Box>

	<ofl:Box title="REPLAY LAST GAMES" className="260" style="height: 198px; padding: 20px 0px 0px 0px">
		<c:set var="line" value="1"/>
		<table class="ltable">
			<col width="35%"/>
			<col width="45%"/>
			<col width="20%"/>
			<tr>
				<th>Date</th>
				<th>Opponent</th>
				<th>Result</th>
			</tr>						
			<c:forEach items="${gamesPlayed}" var="g">
				<tr>
					<td class="lline${line}_left">${g.winningDateString}</td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/UserStats.do"><c:param name="userId" value="${g.opponentId}"/></c:url>" class="llink"><c:out value="${g.opponentName}"/></a></td>
					<td class="lline${line}_right"><a href="<c:url value="${applicationScope.appUrl}/GameReplay.do"><c:param name="gameId" value="${g.id}"/></c:url>" class="llink">${g.result}</a></td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="3"></td></tr>
		</table>
	</ofl:Box>

	<ofl:Box title="DAMAGE RECEIVED" className="260" style="height: 198px; padding: 20px 0px 0px 0px">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Unit</th>
				<th>Deaths</th>
				<th># Def</th>
				<th>Dmg</th>
			</tr>						
			<c:forEach items="${unitDefendStats}" var="s" end="10">
				<tr>
					<td class="lline${line}_left"><c:out value="${s.unitName}"/></td>
					<td class="lline${line}">${s.deaths}</td>
					<td class="lline${line}">${s.numDefends}</td>
					<td class="lline${line}_right">${s.damageReceived}</td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="4"></td></tr>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
