<%@ page contentType="text/html; charset=UTF-8" session="false" %>
<%@ page import="onlinefrontlines.*" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<meta property="og:title" content="Facebook Canvas"/>
		<meta property="og:type" content="game"/>
		<meta property="og:image" content="http://www.onlinefrontlines.com/OnlineFrontlines/assets/icon.png"/>
		<meta property="og:site_name" content="Online Frontlines"/>
		<meta property="fb:admins" content="${applicationScope.fbAdminsUid}"/>
		<meta name="keywords" content="free, turn based, strategy, game, play by mail, online, multiplayer, online frontlines"/>		
		<title>
			Online Frontlines - Facebook Canvas 
		</title>
		<link rel="stylesheet" type="text/css" href="styles.jsp"/>
	</head>
	<body>
		<ofl:JavaScript>
			window.open('${targetUrl}', '_blank');
		</ofl:JavaScript>
		<div id="FacebookCanvas_main">
			<ofl:Box title="ONLINE FRONTLINES" className="466">
				Please <a href="${targetUrl}" target="_blank">click here</a> to open this <c:choose><c:when test="${targetIsInvitation}">invitation</c:when><c:otherwise>application</c:otherwise></c:choose> in a separate window.
			</ofl:Box>
		</div>
	</body>
</html>
