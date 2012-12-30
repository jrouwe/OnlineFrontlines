<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Show Invitation"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="SHOW INVITATIONS"/>
</jsp:include>    

<div id="content_center_single_col">
	<c:forEach items="${requestDetails}" var="d">
		<ofl:Box title="INVITATION" className="466">
			<div id="PBMShowInvitation_avatar">
				<img src="https://graph.facebook.com/${d.senderFacebookId}/picture?type=square" alt="User Image"></img>
			</div>
			<div id="PBMShowInvitation_invitation">
				<c:out value="${d.senderName}"/> has sent you a request to play on '<c:out value="${d.data}"/>'.
			</div>
			<div id="request_${d.requestId}" class="PBMShowInvitation_request_div">
				<ofl:form action="PBMAcceptInvitation" method="get">
				    <ofl:hidden name="requestId" value="${d.requestId}"/>
				    <ofl:submit id="PBMShowInvitation_accept" key="accept"/>
				</ofl:form>
				<ofl:form action="PBMDeclineInvitation" method="get">
				    <ofl:hidden name="requestId" value="${d.requestId}"/>
				    <ofl:submit id="PBMShowInvitation_decline" key="decline"/>
				</ofl:form>
			</div>
		</ofl:Box>
	</c:forEach>
	<c:if test="${fn:length(requestDetails)==0}">
		<ofl:Box title="NO INVITATIONS" className="466">
			There are no invitations to show.
		</ofl:Box>
	</c:if>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
