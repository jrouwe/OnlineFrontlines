<%@ page contentType="text/xml; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<response>
	<code>0</code>
	<uid>${userId}</uid>
	<frd>${friends}</frd>
	<rge>${user.hasEmail && user.receiveGameEventsByMail? 1 : 0}</rge>
	<army>${user.armyAsInt}</army>
	<lobby>
		<bimg>${backgroundImageNumber}</bimg>
		<gcid>${tileCountryConfigIds}</gcid>
	</lobby>
	<c:forEach items="${usedCountryConfigs}" var="c">
		<ccfg>
			<id>${c.id}</id>
			<mid>${c.mapId}</mid>
			<name>${c.name}</name>
			<cpt>${c.isCapturePoint? 1 : 0}</cpt>
			<type>${c.countryType.name}</type>
			<desc>${c.countryType.description}</desc>
			<nunit>${c.numUnits}</nunit>
		</ccfg>
	</c:forEach>
</response>