<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/jsp/layout/top.jsp">
	<jsp:param name="title" value="Play Game"/>
	<jsp:param name="mainMenuSelection" value="PLAY NOW"/>
	<jsp:param name="subMenuSelection" value="CONTINUE GAME"/>
</jsp:include>    

<div style="clear: left; float: left">
	<jsp:include page="/WEB-INF/jsp/IncludeSwf.jsp">
		<jsp:param name="width" value="990"/>
		<jsp:param name="height" value="595"/>
		<jsp:param name="swfPath" value="Game#buildNumber.swf"/>
		<jsp:param name="swfParam" value="gameId=${gameId}&amp;localPlayer=${localPlayer}"/>
	</jsp:include>
</div>

<c:if test="${enableLike}">
	<div class="levellikes">
		<div class="levellikeitem">
			<div class="fb-like" data-send="true" data-layout="button_count" data-show-faces="false" data-colorscheme="dark"></div>
		</div>

		<div class="levellikeitem">
			<a href="https://twitter.com/share" class="twitter-share-button" data-text="Check out this replay from #OnlineFrontlines!" data-count="horizontal" data-via="628games">Tweet</a><script type="text/javascript" src="//platform.twitter.com/widgets.js"></script>
		</div>
	</div>
</c:if>

<%@include file="/WEB-INF/jsp/layout/bottom.jsp"%>
