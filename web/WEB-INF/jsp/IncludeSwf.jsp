<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="onlinefrontlines.Constants" %>
<%@ page import="onlinefrontlines.auth.AutoAuth" %>
<%@ page import="onlinefrontlines.web.WebAction" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>

<%
	String appUrl = (String)application.getAttribute("appUrl");
	String assetsUrl = (String)application.getAttribute("assetsUrl");
	String imagesUrl = (String)application.getAttribute("imagesUrl");
	
	String appUrlEncoded = URLEncoder.encode(appUrl, "UTF-8");
	String assetsUrlEncoded = URLEncoder.encode(assetsUrl, "UTF-8");
	String imagesUrlEncoded = URLEncoder.encode(imagesUrl, "UTF-8");

	WebAction action = (WebAction)request.getAttribute(Constants.CURRENT_ACTION);

	String fullSwf = assetsUrl + "/" + (String)request.getParameter("swfPath") 
					+ "?" + (String)request.getParameter("swfParam") 
					+ "&amp;appUrl=" + appUrlEncoded
					+ "&amp;assetsUrl=" + assetsUrlEncoded		
					+ "&amp;imagesUrl=" + imagesUrlEncoded
					+ "&amp;showHelpBalloons=" + (action.user != null? (action.user.showHelpBalloons? 1 : 0) : 1);		

	String auth = action.user != null? AutoAuth.generateAuthenticationString(action.user, Constants.AUTH_TIME_DEFAULT) : "";
%>

<ofl:JavaScript>
	function getAuthString()
	{
		return '<%= auth %>';
	}
</ofl:JavaScript>
	
<object 
	id="FlashApp"
	classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
	width="${param.width}" 
	height="${param.height}" 
	codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
	
	<param name="movie" value="<%= fullSwf %>"/>
	<param name="play" value="true"/>
	<param name="loop" value="true"/>
	<param name="quality" value="high"/>
	<param name="allowScriptAccess" value="always"/>
	
	<embed 
		src="<%= fullSwf %>" 
		width="${param.width}" 
		height="${param.height}" 
		play="true" 
		loop="true" 
		quality="high" 
		allowScriptAccess="always"
		type="application/x-shockwave-flash"
		pluginspage="http://www.adobe.com/go/getflashplayer">
	</embed>
	
</object>
