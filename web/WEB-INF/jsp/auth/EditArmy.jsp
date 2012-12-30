<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Edit Army"/>
	<jsp:param name="mainMenuSelection" value="PROFILE"/>
	<jsp:param name="subMenuSelection" value="EDIT PROFILE"/>
</jsp:include>    

<div id="content_center_single_col">
	<ofl:Box title="SELECT ARMY" className="466">
		<ofl:actionerror/>
		
		<ofl:form action="DoEditArmy">
			<ofl:select key="army" list="${armies}" value="${army}"/>
			<ofl:hidden name="redirect" value="${redirect}"/>
		    <ofl:submit key="save"/>
		</ofl:form>

		<p>After you select your army you can never change it again.</p>
	</ofl:Box>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
