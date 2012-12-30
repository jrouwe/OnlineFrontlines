<%@ page contentType="text/xml; charset=UTF-8" session="false" %>
<%@ taglib prefix="ofl" uri="/WEB-INF/taglib.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<response>
	<code>0</code>
	<c:forEach items="${countryConfigs}" var="c">
		<ccfg>
			<id>${c.id}</id>
			<name><c:out value="${c.name}"/></name>
		</ccfg>
	</c:forEach>
	<lobby>
		<bimg>${backgroundImageNumber}</bimg>
		<gcid>${tileCountryConfigIds}</gcid>
	</lobby>
</response>