<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Screenshots"/>
	<jsp:param name="mainMenuSelection" value="HELP"/>
	<jsp:param name="subMenuSelection" value="SCREENSHOTS"/>
</jsp:include>    

<%! int numScreens = 6; %>

<ofl:JavaScript>
	function selectImage(n)
	{
		document.getElementById("screen").src = "${applicationScope.assetsUrl}/screens/screenshot" + n + ".png";
	}
</ofl:JavaScript>

<div class="screens_nav">
	<div style="width: <%= numScreens * (166 + 10) + 10 %>px">
		<% for (int i = 1; i <= numScreens; ++i) { %>
			<div class="screens_thumb">
				<a href="select" onclick="selectImage(<%= i %>); return false;">
					<img src="${applicationScope.assetsUrl}/screens/thumb<%= i %>.jpg" alt="thumbnail"/>
				</a>
			</div>
		<% } %>
	</div>		
</div>
<div class="screens">	
	<img class="screens_image" id="screen" src="${applicationScope.assetsUrl}/screens/screenshot1.png" alt="screenshot"/>
</div>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
