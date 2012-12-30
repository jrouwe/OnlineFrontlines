<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Edit Profile"/>
	<jsp:param name="mainMenuSelection" value="PROFILE"/>
	<jsp:param name="subMenuSelection" value="EDIT PROFILE"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="EDIT PROFILE" className="466">
		<ofl:actionerror/>
		
		<ofl:form action="DoEditProfile" method="post">
		    <ofl:textfield key="email" value="${email}" maxlength="255"/>
			<ofl:checkbox key="receiveGameEventsByMail" value="${receiveGameEventsByMail}"/>
		    <ofl:textfield key="realname" value="${realname}" maxlength="255"/>
		    <ofl:textfield key="country" value="${country}" maxlength="255"/>
		    <ofl:textfield key="city" value="${city}" maxlength="255"/>
		    <ofl:textfield key="website" value="${website}" maxlength="255"/>
		    <ofl:checkbox key="autoDeclineFriendlyDefender" value="${autoDeclineFriendlyDefender}"/>
		    <ofl:checkbox key="autoDefendOwnedCountry" value="${autoDefendOwnedCountry}"/>
		    <ofl:checkbox key="showHelpBalloons" value="${showHelpBalloons}"/>
		    <ofl:submit key="save"/>
		</ofl:form>
	</ofl:Box>
	<c:if test="${army == 'none'}">
		<ofl:Box title="ACTIONS" className="466">
			<table class="gtable">
				<ofl:Go title="Select Army"><c:url value="${applicationScope.appUrl}/EditArmy.do"><c:param name="redirect" value="${applicationScope.appUrl}/EditProfile.do"/></c:url></ofl:Go>
			</table>
		</ofl:Box>
	</c:if>		
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
