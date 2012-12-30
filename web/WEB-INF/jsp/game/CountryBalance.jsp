<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Country Balance"/>
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

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
