<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Join Game"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="JOIN"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="JOIN GAME" className="466">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Game Name</th>
				<th>Player 1</th>
				<th>Join</th>
			</tr>
			<c:forEach items="${games}" var="g">
				<tr>
					<td class="lline${line}_left"><c:out value="${g.countryConfigName}"/></td>
					<td class="lline${line}"><c:out value="${g.player1Name}"/></td>
					<td class="lline${line}_right"><a href="<c:url value="${applicationScope.appUrl}/GameDoJoin.do"><c:param name="gameId" value="${g.id}"/></c:url>">Join</a></td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="3"></td></tr>
		</table>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
