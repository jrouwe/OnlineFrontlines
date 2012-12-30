<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Lobby List"/>
	<jsp:param name="mainMenuSelection" value="ADMIN"/>
	<jsp:param name="subMenuSelection" value="LIST"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="LOBBY LIST" className="466">
		<c:set var="line" value="1"/>
		<table class="ltable" style="table-layout: auto">
			<tr>
				<th>Lobby Name</th>
				<th>Properties</th>
				<th>Edit</th>
				<th>Delete</th>
			</tr>
			<c:forEach items="${lobbies}" var="l">
				<tr>
					<td class="lline${line}_left"><c:out value="${l.name}"/></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/LobbyProperties.do"><c:param name="lobbyId" value="${l.id}"/></c:url>">Properties</a></td>
					<td class="lline${line}"><a href="<c:url value="${applicationScope.appUrl}/LobbyEdit.do"><c:param name="lobbyId" value="${l.id}"/></c:url>">Edit</a></td>
					<td class="lline${line}_right"><a href="<c:url value="${applicationScope.appUrl}/LobbyDelete.do"><c:param name="lobbyId" value="${l.id}"/></c:url>" onclick="return confirm('Are you sure you want to delete this lobby?')">Delete</a></td>
				</tr>
				<c:set var="line" value="${line == 1? 2 : 1}"/>
			</c:forEach>
			<tr><td class="lline${line}" colspan="4"></td></tr>
		</table>
	</ofl:Box>

	<p><a href="${applicationScope.appUrl}/LobbyCreate.do">Create Lobby</a></p>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
