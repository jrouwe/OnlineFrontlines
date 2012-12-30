<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Game Balance"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="BALANCE"/>
</jsp:include>    

<table class="ptable">
	<c:forEach items="${balanceTable}" var="t">
		<tr>
			<c:forEach items="${t}" var="i">
				<td>${i}</td>
			</c:forEach>
		</tr>
	</c:forEach>	
</table>

<p>
Horizontal line is attacker, vertical is defender.<br>
DMG = Average armour loss of the defending unit in first attack when both units had full health and ammo before the attack (units are 1 tile apart). Second number is the standard deviation. [MIN, MAX] indicates the minimum and maximum values.<br>
WIN = Percentage of the time the unit wins (other unit is destroyed first) in a situation where attacker and defender are 1 tile apart and use all their action points to kill eachother.<br>
#RND = Average number of rounds needed in the situation described above. Second number is the standard deviation. [MIN, MAX] indicates the minimum and maximum values.
</p>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
