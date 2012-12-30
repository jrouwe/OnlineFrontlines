<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%! int numScreens = 7; %>

<% Random r = new Random(); %>

<c:choose>
	<c:when test="${user != null}">
		<ofl:Box title="SOCIAL" className="260_dark" style="height: 218px; padding: 0px 0px 0px 0px; overflow: visible;">
			<div id="facebook">
				<a href="http://www.facebook.com/onlinefrontlines"><img src="${applicationScope.assetsUrl}/facebook.png" alt="Facebook"/></a>

				<div class="fb-like" data-href="http://www.facebook.com/onlinefrontlines" data-send="true" data-layout="button_count" data-width="450" data-show-faces="false" data-colorscheme="dark"></div>
			</div>
			<div id="twitter">
				<a href="http://www.twitter.com/628games/"><img src="${applicationScope.assetsUrl}/twitter.png" alt="Twitter"/></a>
				
				<a href="https://twitter.com/share" class="twitter-share-button" data-url="http://www.onlinefrontlines.com/" data-text="Check out this cool web game called #OnlineFrontlines!" data-count="horizontal" data-via="628games">Tweet</a><script type="text/javascript" src="//platform.twitter.com/widgets.js"></script>
			</div>
		</ofl:Box>
	</c:when>
	<c:otherwise>
		<ofl:Box title="SCREEN SHOT" className="260_dark" style="height: 218px; padding: 0px 0px 0px 0px">
			<a href="${applicationScope.appUrl}/HelpScreens.do" style="border: 0px"><img src="${applicationScope.assetsUrl}/screens/tips_screenshot<%= (Math.abs(r.nextInt()) % numScreens) + 1 %>.png" alt="screen shot"/></a>
		</ofl:Box>
	</c:otherwise>
</c:choose>
		